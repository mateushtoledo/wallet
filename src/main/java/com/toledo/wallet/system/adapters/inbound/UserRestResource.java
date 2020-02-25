package com.toledo.wallet.system.adapters.inbound;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.system.dto.UserDTO;
import com.toledo.wallet.system.ports.inbound.UserServicePort;

@RestController
@RequestMapping("/users")
public class UserRestResource extends AbstractResource {
	@Autowired
	private UserServicePort userService;
	
	@PostMapping
	public ResponseEntity<Void> save(
		@Valid @RequestBody UserDTO userData,
		BindingResult br
	) {
		// Validate binding
		validateBinding(br);
		// Save user
		User entity = userService.save(userData.toEntity());
		return responseAsCreated(entity.getId());
	}
}
