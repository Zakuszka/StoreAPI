package com.store.storeapi.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import com.store.storeapi.pojo.security.AuthUserDetails;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Autowired
	private final UserDetailsService userDetailsService;

	@Autowired
	public CustomJwtAuthenticationConverter(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(jwt.getClaimAsString("username"));

		if (userDetails instanceof AuthUserDetails) {
			return new MyUserJwtAuthenticationToken((AuthUserDetails) userDetails, userDetails.getAuthorities());
		} else {
			return null;
		}
	}

}
