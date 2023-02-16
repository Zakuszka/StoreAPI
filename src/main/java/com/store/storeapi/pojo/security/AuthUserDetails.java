package com.store.storeapi.pojo.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.store.storeapi.pojo.User;

public class AuthUserDetails implements UserDetails {

	private static final long serialVersionUID = -6232711674635140590L;

	private int userId;
	private String username;
	private String password;
	private boolean enabled;
	private Set<GrantedAuthority> grantedAuthorities;

	public AuthUserDetails(User user) {
		super();
		this.userId = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.enabled = user.isEnabled();

		Set<GrantedAuthority> authorities = new HashSet<>();

		user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
		this.grantedAuthorities = authorities;
	}

	public int getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
