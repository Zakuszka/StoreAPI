package com.store.storeapi.bl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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

		UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

		if (!passwordEncode.matchPassword(loginRequest.getPassword(), userDetails.getPassword())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PASSWORD.getCode(), ErrorMessage.MISSING_OR_INVALID_PASSWORD.getMessage());
		}

		Map<String, String> claims = new HashMap<>();
		claims.put("username", loginRequest.getUsername());
		claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" ")));
		claims.put("userId", String.valueOf(1));

		return new TokenResponse(jwtTokenProvider.generatePasswordToken(claims), jwtTokenProvider.getPasswordTokenExpirationInMinutes());
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
		user.setEnabled(true);

		List<Role> roles = new ArrayList<>();

		userRequest.getRoles().forEach(role -> roles.add(roleService.getByName(role)));

		user.setRoles(roles);

		return new UserDetailsResponse(userService.save(user));
	}

	public UserDetailsResponse getUserDetails(AuthUserDetails authUserDetails) throws BaseException {

		User user = userService.getById(authUserDetails.getUserId());

		if (user == null) {
			LOGGER.warn(String.format("[getUserDetails] User with id: %d does not exists", authUserDetails.getUserId()));
			throw new BadRequestException(ErrorMessage.USER_DOES_NOT_EXIST.getCode(), ErrorMessage.USER_DOES_NOT_EXIST.getMessage());
		}

		return new UserDetailsResponse(user);
	}

	private void validateParameters(UserRequest userRequest) throws BaseException {

		if (!validityService.validateEmailAddress(userRequest.getUsername())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_EMAIL.getCode(), ErrorMessage.MISSING_OR_INVALID_EMAIL.getMessage());
		}

		if (!validityService.validateEmailAddress(userRequest.getPassword())) {
			throw new BadRequestException(ErrorMessage.MISSING_OR_INVALID_PASSWORD.getCode(), ErrorMessage.MISSING_OR_INVALID_PASSWORD.getMessage());
		}

		if (!validityService.validateEmailAddress(userRequest.getPhoneNumber())) {
			throw new BadRequestException(ErrorMessage.INVALID_PHONE_NUMBER.getCode(), ErrorMessage.INVALID_PHONE_NUMBER.getMessage());
		}

	}

}
