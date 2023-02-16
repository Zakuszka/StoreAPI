package com.store.storeapi.exception;

public abstract class BaseException extends Exception {

	private static final long serialVersionUID = -3651556786728867641L;

	private final String code;
	private final String description;

	protected BaseException(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public abstract Integer getHttpStatusCode();

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

}
