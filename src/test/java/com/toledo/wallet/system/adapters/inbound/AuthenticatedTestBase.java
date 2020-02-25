package com.toledo.wallet.system.adapters.inbound;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.business.exceptions.UserNotFoundException;
import com.toledo.wallet.system.ports.inbound.UserServicePort;
import com.toledo.wallet.system.security.domain.SpringSecurityUser;
import com.toledo.wallet.system.security.util.JwtTokenUtil;

public abstract class AuthenticatedTestBase {

	protected static final String EXPECTED_EMAIL = "user@test.net";
	protected static final String EXPECTED_PASSWORD = "123456";
	protected static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	protected static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

	@MockBean
	protected UserDetailsService authService;
	@MockBean
	protected UserServicePort userService;
	@Autowired
	protected BCryptPasswordEncoder passwordEncoder;
	@Autowired
	protected Environment env;

	public void mockAuthentication() {
		
		Mockito.when(userService.findByEmail(Mockito.anyString()))
			.thenThrow(new UserNotFoundException("Usuário não encontrado!"));
		
		Mockito.when(userService.findByEmail(Mockito.eq(EXPECTED_EMAIL)))
			.thenReturn(getUserMock());
		
		Mockito.when(authService.loadUserByUsername(Mockito.anyString()))
			.thenThrow(new UserNotFoundException("Usuário não encontrado!"));
		
		Mockito.when(authService.loadUserByUsername(Mockito.eq(EXPECTED_EMAIL)))
			.thenReturn(getAuthenticatedUserMock());
	}

	protected SpringSecurityUser getAuthenticatedUserMock() {
		String encodedPassword = passwordEncoder.encode(EXPECTED_PASSWORD);
		return new SpringSecurityUser(1L, EXPECTED_EMAIL, encodedPassword);
	}
	
	protected User getUserMock() {
		return new User(1L, "RANDOM NAME", EXPECTED_EMAIL, EXPECTED_PASSWORD, null);
	}

	protected String getAuthorizationToken() {
		JwtTokenUtil jwtUtil = new JwtTokenUtil(env);
		String jwt = jwtUtil.getToken(new SpringSecurityUser(1L, EXPECTED_EMAIL, EXPECTED_PASSWORD));

		return AUTHORIZATION_HEADER_PREFIX.concat(jwt);
	}
}
