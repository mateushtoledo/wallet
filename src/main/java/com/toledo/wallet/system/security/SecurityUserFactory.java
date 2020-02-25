package com.toledo.wallet.system.security;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.system.security.domain.SpringSecurityUser;

public class SecurityUserFactory {
	
	public static SpringSecurityUser create(User authenticated) {
		return new SpringSecurityUser(authenticated.getId(), authenticated.getEmail(), authenticated.getPassword());
	}
	
}
