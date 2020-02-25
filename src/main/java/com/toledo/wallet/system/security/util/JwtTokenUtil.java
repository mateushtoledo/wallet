package com.toledo.wallet.system.security.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_ROLE = "role";
	static final String CLAIM_KEY_AUDIENCE = "audience";
	static final String CLAIM_KEY_CREATED = "created";

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;
	
	public JwtTokenUtil() {}
	
	public JwtTokenUtil(Environment env) {
		this.expiration = Long.parseLong(env.getProperty("jwt.expiration"));
		this.secret = env.getProperty("jwt.secret");
	}

	public String getUsernameFromToken(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			return claims.getSubject();
		} catch (Exception ex) {
			return null;
		}
	}

	public Date getExpirationDateFromToken(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			return claims.getExpiration();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean validToken(String token) {
		return !expiredToken(token);
	}

	public String getToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		claims.put(CLAIM_KEY_CREATED, new Date());

		return generateToken(claims);
	}

	private Claims getClaimsFromToken(String token) {
		try {
			return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			return null;
		}
	}

	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + expiration * 1000);
	}

	private boolean expiredToken(String token) {
		Date expirationDate = this.getExpirationDateFromToken(token);
		if (expirationDate == null) {
			return false;
		}
		return expirationDate.before(new Date());
	}

	private String generateToken(Map<String, Object> claims) {
		return Jwts.builder()
			.setClaims(claims)
			.setExpiration(generateExpirationDate())
			.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
}