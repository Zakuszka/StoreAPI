package com.store.storeapi.ws.response;

public class TokenResponse {

	private String token;
	private int expiresIn;

	public TokenResponse(String token, int expiresIn) {
		super();
		this.token = token;
		this.expiresIn = expiresIn;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

}
