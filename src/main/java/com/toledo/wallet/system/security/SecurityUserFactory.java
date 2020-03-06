package com.toledo.wallet.system.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.system.security.domain.SpringSecurityUser;

public class SecurityUserFactory {
	
	public static SpringSecurityUser create(User authenticated) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(authenticated.getType().toString()));
		return new SpringSecurityUser(authenticated.getId(), authenticated.getEmail(), authenticated.getPassword(), authorities);
	}
	
}
