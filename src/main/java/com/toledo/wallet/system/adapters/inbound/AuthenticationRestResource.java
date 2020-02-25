package com.toledo.wallet.system.adapters.inbound;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toledo.wallet.system.dto.AuthenticationDTO;
import com.toledo.wallet.system.security.util.JwtTokenUtil;

@RestController
@RequestMapping("/auth")
public class AuthenticationRestResource extends AbstractResource {
	@Autowired
	private AuthenticationManager manager;
	@Autowired
	private JwtTokenUtil jwtUtil;
	@Autowired
	private UserDetailsService service;
	
	@PostMapping
	ResponseEntity<Void> authenticate(@RequestBody @Valid AuthenticationDTO authData, BindingResult br) {
		// Validate binding
		validateBinding(br);
		
		// Authenticate user
		Authentication auth = manager.authenticate(new UsernamePasswordAuthenticationToken(authData.getEmail(), authData.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		// Create authorization token(JWT) to user
		String authorizationToken = getAuthorizationToken(authData.getEmail());
		
		// Response with authorization token
		return ResponseEntity.ok().header("Authorization", authorizationToken).build();
	}
	
	@PostMapping("/refresh")
	ResponseEntity<Void> refreshToken() {
		String authorizationToken = getAuthorizationToken(getAuthenticatedEmail());
		return ResponseEntity.ok().header("Authorization", authorizationToken).build();
	}
	
	private String getAuthorizationToken(String userEmail) {
		UserDetails authenticated = service.loadUserByUsername(userEmail);
		String jwtToken = jwtUtil.getToken(authenticated);
		return "Bearer ".concat(jwtToken);
	}
}
