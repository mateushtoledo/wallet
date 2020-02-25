package com.toledo.wallet.system.adapters.inbound;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.system.exceptions.ValidationErrorException;
import com.toledo.wallet.system.ports.inbound.UserServicePort;

public abstract class AbstractResource {
	
	protected ResponseEntity<Void> responseAsCreated(Long id) {
		// Create resource URI
		URI resourceUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
		// Response as created
		return ResponseEntity.created(resourceUri).build();
	}

	protected void validateBinding(BindingResult br) {
		if (br.hasErrors()) {
			List<String> validationErrors = br.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
			throw new ValidationErrorException(validationErrors);
		}
	}
	
	protected String getAuthenticatedEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	protected User getAuthenticated(UserServicePort userService) {
		return userService.findByEmail(getAuthenticatedEmail());
	}
	
	protected Long getAuthenticatedId(UserServicePort userService) {
		return userService.getIdByEmail(getAuthenticatedEmail());
	}
}
