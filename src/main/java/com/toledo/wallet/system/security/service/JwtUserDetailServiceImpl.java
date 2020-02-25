package com.toledo.wallet.system.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.system.ports.inbound.UserServicePort;
import com.toledo.wallet.system.security.SecurityUserFactory;

@Service
public class JwtUserDetailServiceImpl implements UserDetailsService {
	@Autowired
	private UserServicePort service;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			User authenticated = service.findByEmail(email);
			return SecurityUserFactory.create(authenticated);
		} catch (Exception e) {
			throw new UsernameNotFoundException("O e-mail informado é inválido!");
		}
	}
}
