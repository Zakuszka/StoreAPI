package com.store.storeapi.config.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;

public class PasswordEncoderFactory {

	public PasswordEncoder createDelegatingPasswordEncoder(String passwordSecret) {
		String encodingId = "pbkdf2";
		Map<String, PasswordEncoder> encoders = new ConcurrentHashMap<String, PasswordEncoder>();
		Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder(passwordSecret, 50000, 256);
		pbkdf2PasswordEncoder.setAlgorithm(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
		encoders.put(encodingId, pbkdf2PasswordEncoder);
		return new DelegatingPasswordEncoder(encodingId, encoders);
	}

}
