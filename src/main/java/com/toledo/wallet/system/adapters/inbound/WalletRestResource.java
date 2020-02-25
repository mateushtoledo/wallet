package com.toledo.wallet.system.adapters.inbound;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.system.dto.WalletDTO;
import com.toledo.wallet.system.ports.inbound.UserServicePort;
import com.toledo.wallet.system.ports.inbound.WalletServicePort;

@RestController
@RequestMapping("/wallets")
public class WalletRestResource extends AbstractResource {
	@Autowired
	private WalletServicePort service;
	@Autowired
	private UserServicePort userService;
	
	@PostMapping
	public ResponseEntity<Void> save(
		@Valid @RequestBody WalletDTO walletData,
		BindingResult br
	) {
		// Validate binding
		validateBinding(br);
		// Convert DTO to Wallet
		Wallet entity = walletData.toEntity();
		entity.setUser(getAuthenticated(userService));
		// Save and response as created
		entity = service.save(entity);
		return responseAsCreated(entity.getId());
	}
}
