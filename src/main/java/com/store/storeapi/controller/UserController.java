package com.store.storeapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/users")
public class UserController {

	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserBL userBL;

	@PostMapping(value = "/login", produces = "application/json")
	@ApiOperation(value = "Login with username and password", response = TokenResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = TokenResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) throws BaseException {

		return new ResponseEntity<>(userBL.login(loginRequest), HttpStatus.OK);
	}

	@GetMapping(value = "/details", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
	@ApiOperation(value = "Get User details", response = UserDetailsResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = UserDetailsResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<UserDetailsResponse> getUserDetails(@AuthenticationPrincipal AuthUserDetails authUserDetails) throws BaseException {

		return new ResponseEntity<>(userBL.getUserDetails(authUserDetails), HttpStatus.OK);
	}

	@PostMapping(value = "", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
	@ApiOperation(value = "Create User account", response = UserDetailsResponse.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = UserDetailsResponse.class),
			@ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
	public ResponseEntity<UserDetailsResponse> createUser(@AuthenticationPrincipal ClaimAccessor jwt, @RequestBody UserRequest userRequest)
			throws BaseException {

		return new ResponseEntity<>(userBL.createUser(userRequest), HttpStatus.CREATED);
	}

}
