package com.store.storeapi.bl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.store.storeapi.config.security.PasswordEncode;
import com.store.storeapi.exception.BadRequestException;
import com.store.storeapi.exception.BaseException;
import com.store.storeapi.pojo.Role;
import com.store.storeapi.pojo.User;
import com.store.storeapi.pojo.security.AuthUserDetails;
import com.store.storeapi.security.JwtTokenProvider;
import com.store.storeapi.service.db.RoleService;
import com.store.storeapi.service.db.UserService;
import com.store.storeapi.service.util.ValidityService;
import com.store.storeapi.util.Constants;
import com.store.storeapi.ws.error.ErrorMessage;
import com.store.storeapi.ws.request.LoginRequest;
import com.store.storeapi.ws.request.UserRequest;
import com.store.storeapi.ws.response.TokenResponse;
import com.store.storeapi.ws.response.UserDetailsResponse;

@Service
public class UserBL {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserBL.class);

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncode passwordEncode;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private ValidityService validityService;

	public TokenResponse login(LoginRequest loginRequest) throws BaseException {

		AuthUserDetails userDetails = (AuthUserDetails) userDetailsService.loadUserByUsername(loginRequest.getUsername());

		if (!passwordEncode.matchPassword(loginRequest.getPassword(), userDetails.getPassword())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PASSWORD.getCode(), ErrorMessage.MISSING_OR_INVALID_PASSWORD.getMessage());
		}

		return doLogin(loginRequest.getUsername(), userDetails.getUserId(), userDetails.getAuthorities());
	}

	public UserDetailsResponse createUser(UserRequest userRequest) throws BaseException {

		validateParameters(userRequest);

		User user = userService.getByUsername(userRequest.getUsername());

		if (user != null) {
			LOGGER.warn(String.format("[createUser] User with username: %s already exists with id: %d", userRequest.getUsername(), user.getId()));
			throw new BadRequestException(ErrorMessage.USER_WITH_USERNAME_EXISTS.getCode(), ErrorMessage.USER_WITH_USERNAME_EXISTS.getMessage());
		}

		user = new User();
		user.setPassword(passwordEncode.encodePassword(userRequest.getPassword()));
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setPhoneNumber(userRequest.getPhoneNumber());
		user.setAddress(userRequest.getAddress());
		user.setEnabled(true);

		List<Role> roles = new ArrayList<>();
		userRequest.getRoles().forEach(role -> roles.add(roleService.getByName(role)));
		user.setRoles(roles);

		user = userService.save(user);

		LOGGER.info(String.format("[createUser] New User created with username: %s and id: %d", user.getUsername(), user.getId()));

		return new UserDetailsResponse(user);
	}

	public UserDetailsResponse getUserDetails(AuthUserDetails authUserDetails) throws BaseException {

		User user = userService.getById(authUserDetails.getUserId());

		if (user == null) {
			LOGGER.warn(String.format("[getUserDetails] User with id: %d does not exists", authUserDetails.getUserId()));
			throw new BadRequestException(ErrorMessage.USER_NOT_EXISTS.getCode(), ErrorMessage.USER_NOT_EXISTS.getMessage());
		}

		return new UserDetailsResponse(user);
	}

	public UserDetailsResponse updateUserDetails(AuthUserDetails authUserDetails, UserRequest userRequest) throws BaseException {
		User user = userService.getById(authUserDetails.getUserId());

		if (user == null) {
			LOGGER.warn(String.format("[updateUserDetails] User with id: %d does not exists", authUserDetails.getUserId()));
			throw new BadRequestException(ErrorMessage.USER_NOT_EXISTS.getCode(), ErrorMessage.USER_NOT_EXISTS.getMessage());
		}

		if (!validityService.validatePhoneNumber(userRequest.getPhoneNumber())) {
			throw new BadRequestException(ErrorMessage.INVALID_PHONE_NUMBER.getCode(), ErrorMessage.INVALID_PHONE_NUMBER.getMessage());
		}

		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setPhoneNumber(userRequest.getPhoneNumber());
		user.setAddress(userRequest.getAddress());

		user = userService.save(user);

		LOGGER.info(String.format("[updateUserDetails] Successfully updated the User with username: %s and id: %d", user.getUsername(), user.getId()));

		return new UserDetailsResponse(user);
	}

	public TokenResponse changeUserEmail(AuthUserDetails authUserDetails, UserRequest userRequest) throws BaseException {
		User user = userService.getById(authUserDetails.getUserId());

		if (user == null) {
			LOGGER.warn(String.format("[changeUserEmail] User with id: %d does not exists", authUserDetails.getUserId()));
			throw new BadRequestException(ErrorMessage.USER_NOT_EXISTS.getCode(), ErrorMessage.USER_NOT_EXISTS.getMessage());
		}

		if (!validityService.validateEmailAddress(userRequest.getUsername())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_EMAIL.getCode(), ErrorMessage.MISSING_OR_INVALID_EMAIL.getMessage());
		}

		user.setUsername(userRequest.getUsername());
		userService.save(user);

		LOGGER.info(String.format("[changeUserEmail] Successfully changed the email of the User with id: %d", user.getId()));

		return doLogin(userRequest.getUsername(), authUserDetails.getUserId(), authUserDetails.getAuthorities());
	}

	public TokenResponse changeUserPassword(AuthUserDetails authUserDetails, UserRequest userRequest) throws BaseException {
		User user = userService.getById(authUserDetails.getUserId());

		if (user == null) {
			LOGGER.warn(String.format("[changeUserPassword] User with id: %d does not exists", authUserDetails.getUserId()));
			throw new BadRequestException(ErrorMessage.USER_NOT_EXISTS.getCode(), ErrorMessage.USER_NOT_EXISTS.getMessage());
		}

		if (!validityService.validateUserPassword(userRequest.getPassword())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PASSWORD.getCode(), ErrorMessage.MISSING_OR_INVALID_PASSWORD.getMessage());
		}

		user.setPassword(passwordEncode.encodePassword(userRequest.getPassword()));
		userService.save(user);

		LOGGER.info(String.format("[changeUserPassword] Successfully changed the password of the User with id: %d", user.getId()));

		return doLogin(userRequest.getUsername(), authUserDetails.getUserId(), authUserDetails.getAuthorities());
	}

	public List<UserDetailsResponse> listUsers(Integer limit, Long offset) throws BaseException {

		if (offset == null) {
			offset = Constants.DEFAULT_PAGING_OFFSET;
		}

		if (limit != null && limit.intValue() == -1) {
			limit = Constants.MAXIMUM_PAGING_LIMIT;
		}

		if (limit == null || limit == 0) {
			limit = Constants.DEFAULT_PAGING_LIMIT;
		}

		Page<User> users = userService.listUsers(limit, offset);

		if (users == null || users.isEmpty()) {
			return Collections.emptyList();
		}

		return users.stream().map(user -> new UserDetailsResponse(user)).collect(Collectors.toList());
	}

	private void validateParameters(UserRequest userRequest) throws BaseException {

		if (!validityService.validateEmailAddress(userRequest.getUsername())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_EMAIL.getCode(), ErrorMessage.MISSING_OR_INVALID_EMAIL.getMessage());
		}

		if (!validityService.validateUserPassword(userRequest.getPassword())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PASSWORD.getCode(), ErrorMessage.MISSING_OR_INVALID_PASSWORD.getMessage());
		}

		if (!validityService.validatePhoneNumber(userRequest.getPhoneNumber())) {
			throw new BadRequestException(ErrorMessage.INVALID_PHONE_NUMBER.getCode(), ErrorMessage.INVALID_PHONE_NUMBER.getMessage());
		}
	}

	private TokenResponse doLogin(String username, int userId, Collection<? extends GrantedAuthority> collection) throws BaseException {
		Map<String, String> claims = new HashMap<>();
		claims.put(Constants.USERNAME, username);
		claims.put(Constants.AUTHORITIES_CLAIM_NAME, collection.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" ")));
		claims.put(Constants.USER_ID, String.valueOf(userId));

		LOGGER.info(String.format("[doLogin] Create new authentication token for User with id: %d", userId));

		return new TokenResponse(jwtTokenProvider.generatePasswordToken(claims), jwtTokenProvider.getPasswordTokenExpirationInMinutes());
	}

}
