package com.store.storeapi.bl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.store.storeapi.config.security.PasswordEncode;
import com.store.storeapi.exception.BadRequestException;
import com.store.storeapi.pojo.Role;
import com.store.storeapi.pojo.User;
import com.store.storeapi.pojo.security.AuthUserDetails;
import com.store.storeapi.security.JwtTokenProvider;
import com.store.storeapi.service.db.RoleService;
import com.store.storeapi.service.db.UserService;
import com.store.storeapi.service.util.ValidityService;
import com.store.storeapi.ws.error.ErrorMessage;
import com.store.storeapi.ws.request.LoginRequest;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class UserBLTest {

	@TestConfiguration
	static class UserBLTestContextConfiguration {

		@Bean
		public UserBL userBL() {
			return new UserBL();
		}
	}

	@Autowired
	private UserBL userBL;

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private PasswordEncode passwordEncode;

	@MockBean
	private UserService userService;

	@MockBean
	private RoleService roleService;

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private ValidityService validityService;

	@Test
	public void loginWhenMissingUsernameThenThrowException() {
		LoginRequest loginRequest = new LoginRequest();

		assertThatThrownBy(() -> userBL.login(loginRequest)).isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("code", ErrorMessage.MISSING_MANDATORY_PARAMETER.getCode())
				.hasFieldOrPropertyWithValue("description", ErrorMessage.MISSING_MANDATORY_PARAMETER.getMessage());
	}

	@Test
	public void loginWhenMissingOrInvalidPasswordThenThrowException() {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("test@test.com");
		loginRequest.setPassword("Test123");

		User user = new User();
		user.setUsername(loginRequest.getUsername());
		user.setPassword("{pbkdf2}test");

		Role role = new Role();
		role.setName("USER");

		List<Role> roles = new ArrayList<>();
		roles.add(role);
		user.setRoles(roles);

		AuthUserDetails authUserDetails = new AuthUserDetails(user);

		Mockito.when(roleService.getByName("USER")).thenReturn(role);
		Mockito.when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(authUserDetails);
		Mockito.when(passwordEncode.matchPassword(loginRequest.getPassword(), authUserDetails.getPassword())).thenReturn(false);

		assertThatThrownBy(() -> userBL.login(loginRequest)).isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("code", ErrorMessage.MISSING_OR_INVALID_PASSWORD.getCode())
				.hasFieldOrPropertyWithValue("description", ErrorMessage.MISSING_OR_INVALID_PASSWORD.getMessage());
	}

}
