package com.store.storeapi.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.store.storeapi.dao.UserRepository;
import com.store.storeapi.pojo.User;
import com.store.storeapi.pojo.security.AuthUserDetails;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public AuthUserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DisabledException {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

		if (!user.isEnabled()) {
			throw new DisabledException("User is disabled");
		}

		return new AuthUserDetails(user);
	}

}
