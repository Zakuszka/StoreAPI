package com.store.storeapi.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncode {

	private static final Logger LOGGER = LoggerFactory.getLogger(PasswordEncode.class);

	private PasswordEncoder myPasswordEncoder;

	@Autowired
	public PasswordEncode(PasswordEncoder passwordEncoder) {
		myPasswordEncoder = passwordEncoder;
	}

	public String encodePassword(String password) {
		return myPasswordEncoder.encode(password);
	}

	public boolean matchPassword(String password, String encodedPassword) {
		return myPasswordEncoder.matches(password, encodedPassword);
	}

}
