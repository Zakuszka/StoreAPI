package com.store.storeapi.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.store.storeapi.service.security.CustomJwtAuthenticationConverter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

	@Value("${password-secret}")
	private String passwordSecret;

	private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

	public WebSecurityConfiguration(CustomJwtAuthenticationConverter customJwtAuthenticationConverter) {
		this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
	}

	@Bean
	public SecurityFilterChain api(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll().anyRequest().fullyAuthenticated().and().oauth2ResourceServer().jwt()
				.jwtAuthenticationConverter(customJwtAuthenticationConverter);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoderFactory epc = new PasswordEncoderFactory();
		return epc.createDelegatingPasswordEncoder(passwordSecret);
	}

}
