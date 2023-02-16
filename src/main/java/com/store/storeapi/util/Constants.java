package com.store.storeapi.util;

public final class Constants {

	public static final Long DEFAULT_PAGING_OFFSET = 0L;
	public static final Integer DEFAULT_PAGING_LIMIT = 10;
	public static final Integer MINIMUM_SEARCH_TEXT_LENGTH = 3;
	public static final Integer MAXIMUM_SEARCH_TERM_LENGTH = 20;
	public static final Integer MAXIMUM_PAGING_LIMIT = 10000;
	public static final String AUTHORITIES_CLAIM_NAME = "roles";
	public static final String USERNAME = "username";
	public static final String USER_ID = "userId";

	private Constants() {
		super();
	}

}
