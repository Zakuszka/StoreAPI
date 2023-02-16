package com.store.storeapi.ws.error;

public enum ErrorMessage {

	INTERNAL_SERVER_ERROR("500", "Please contact your administrator"), //
	USER_DOES_NOT_EXIST("1001", "User does not exists"), //
	USER_WITH_USERNAME_EXISTS("1002", "User with username already exists"), //
	MISSING_OR_INVALID_EMAIL("1003", "Missing or invalid email address"), //
	INVALID_PHONE_NUMBER("1004", "Invalid phone number"), //
	MISSING_OR_INVALID_PASSWORD("1005", "Missing or invalid password"), //
	INVALID_TOKEN("1006", "Invalid Change Password Token"), //
	MISSING_MANDATORY_PARAMETER("1007", "Missing mandatory parameter"); //

	private String message;
	private String code;

	ErrorMessage(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

}
