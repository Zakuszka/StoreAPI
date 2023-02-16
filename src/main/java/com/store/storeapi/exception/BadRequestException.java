package com.store.storeapi.exception;

public class BadRequestException extends BaseException {

	private static final long serialVersionUID = -277346529160794428L;
	private static final Integer HTTP_STATUS_CODE = 400;

	public BadRequestException(String code, String description) {
		super(code, description);
	}

	@Override
	public Integer getHttpStatusCode() {
		return HTTP_STATUS_CODE;
	}

}
