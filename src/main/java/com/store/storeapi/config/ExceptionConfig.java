package com.store.storeapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.store.storeapi.exception.BaseException;
import com.store.storeapi.ws.error.ErrorMessage;
import com.store.storeapi.ws.error.ErrorResponse;

@ControllerAdvice
public class ExceptionConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionConfig.class);

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(BaseException ex) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ex.getCode(), ex.getDescription()), HttpStatus.valueOf(ex.getHttpStatusCode()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
		if (ex instanceof org.springframework.security.access.AccessDeniedException) {
			LOGGER.warn("Access is denied.");
			return new ResponseEntity<ErrorResponse>(new ErrorResponse("401", "Access is denied"), HttpStatus.UNAUTHORIZED);
		} else {
			LOGGER.error("Global generic exeption handler fired for exception: ", ex);

			return new ResponseEntity<ErrorResponse>(
					new ErrorResponse(ErrorMessage.INTERNAL_SERVER_ERROR.getCode(), ErrorMessage.INTERNAL_SERVER_ERROR.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
