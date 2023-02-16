package com.store.storeapi.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.store.storeapi.bl.UserBL;
import com.store.storeapi.exception.BaseException;
import com.store.storeapi.pojo.security.AuthUserDetails;
import com.store.storeapi.ws.error.ErrorResponse;
import com.store.storeapi.ws.request.LoginRequest;
import com.store.storeapi.ws.request.UserRequest;
import com.store.storeapi.ws.response.TokenResponse;
import com.store.storeapi.ws.response.UserDetailsResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/users")
public class UserController {

	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserBL userBL;

	@PostMapping(value = "/user/login", produces = "application/json")
	@ApiOperation(value = "Login with username and password", response = TokenResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = TokenResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) throws BaseException {

		return new ResponseEntity<>(userBL.login(loginRequest), HttpStatus.OK);
	}

	@GetMapping(value = "/user", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
	@ApiOperation(value = "Get User details", response = UserDetailsResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = UserDetailsResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<UserDetailsResponse> getUserDetails(@AuthenticationPrincipal AuthUserDetails authUserDetails) throws BaseException {

		return new ResponseEntity<>(userBL.getUserDetails(authUserDetails), HttpStatus.OK);
	}

	@PutMapping(value = "/user", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
	@ApiOperation(value = "Update User details", response = UserDetailsResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = UserDetailsResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<UserDetailsResponse> updateUserDetails(@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody UserRequest userRequest)
			throws BaseException {

		return new ResponseEntity<>(userBL.updateUserDetails(authUserDetails, userRequest), HttpStatus.OK);
	}

	@PutMapping(value = "/user/change-email", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
	@ApiOperation(value = "Change User email", response = TokenResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = TokenResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<TokenResponse> changeUserEmail(@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody UserRequest userRequest)
			throws BaseException {

		return new ResponseEntity<>(userBL.changeUserEmail(authUserDetails, userRequest), HttpStatus.OK);
	}

	@PutMapping(value = "/user/change-password", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
	@ApiOperation(value = "Change User password", response = TokenResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = TokenResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<TokenResponse> changeUserPassword(@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody UserRequest userRequest)
			throws BaseException {

		return new ResponseEntity<>(userBL.changeUserPassword(authUserDetails, userRequest), HttpStatus.OK);
	}

	@PostMapping(value = "/user", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
	@ApiOperation(value = "Create User account", response = UserDetailsResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = UserDetailsResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<UserDetailsResponse> createUser(@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody UserRequest userRequest)
			throws BaseException {

		return new ResponseEntity<>(userBL.createUser(userRequest), HttpStatus.CREATED);
	}

	@PostMapping(value = "", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
	@ApiOperation(value = "List User accounts", response = UserDetailsResponse.class, responseContainer = "List")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = UserDetailsResponse.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<List<UserDetailsResponse>> listUsers(@AuthenticationPrincipal AuthUserDetails authUserDetails,
			@ApiParam(required = false, type = "int", name = "limit") @RequestParam(required = false, value = "limit") Integer limit,
			@ApiParam(required = false, type = "int", name = "offset") @RequestParam(required = false, value = "offset") Long offset) throws BaseException {

		return new ResponseEntity<>(userBL.listUsers(limit, offset), HttpStatus.OK);
	}

}
