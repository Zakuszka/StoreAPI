package com.store.storeapi.service.security;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import com.store.storeapi.pojo.security.AuthUserDetails;

public class MyUserJwtAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 7210328777806561456L;

	private final AuthUserDetails authUserDetails;

	public MyUserJwtAuthenticationToken(AuthUserDetails authUserDetails, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.authUserDetails = authUserDetails;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return "n/a";
	}

	@Override
	public Object getPrincipal() {
		return this.authUserDetails;
	}

}
