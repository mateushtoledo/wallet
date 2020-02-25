package com.toledo.wallet.system.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.toledo.wallet.system.security.util.JwtTokenUtil;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		// It's a bearer token?
		String token = request.getHeader(AUTH_HEADER);
		if (token != null && token.startsWith(BEARER_PREFIX)) {
			token = token.substring(7);
		}
		
		// Try login with user
		String username = jwtTokenUtil.getUsernameFromToken(token);
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails securityUser = this.userDetailsService.loadUserByUsername(username);
			if (jwtTokenUtil.validToken(token)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		
		// Continue processing the request
		chain.doFilter(request, response);
	}
}