package com.store.storeapi.exception;

public class InternalServerErrorException extends BaseException {

	private static final long serialVersionUID = 6716950683344949062L;
	private static final Integer HTTP_STATUS_CODE = 500;

	public InternalServerErrorException(String code, String description) {
		super(code, description);
	}

	@Override
	public Integer getHttpStatusCode() {
		return HTTP_STATUS_CODE;
	}

}
