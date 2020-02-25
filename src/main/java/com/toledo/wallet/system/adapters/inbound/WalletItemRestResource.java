package com.toledo.wallet.system.adapters.inbound;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.business.domain.WalletItem;
import com.toledo.wallet.business.domain.enums.WalletItemType;
import com.toledo.wallet.system.dto.WalletItemDTO;
import com.toledo.wallet.system.ports.inbound.UserServicePort;
import com.toledo.wallet.system.ports.inbound.WalletItemServicePort;
import com.toledo.wallet.system.ports.inbound.WalletServicePort;

@RestController
@RequestMapping(path = "/wallets/{walletId}/items")
public class WalletItemRestResource extends AbstractResource {
	@Autowired
	private WalletServicePort walletService;
	@Autowired
	private UserServicePort userService;
	@Autowired
	private WalletItemServicePort service;
	
	private static final Logger logger = LoggerFactory.getLogger(WalletItemRestResource.class);

	@PostMapping
	public ResponseEntity<Void> save(
		@PathVariable(name = "walletId") Long walletId,
		@RequestBody @Valid WalletItemDTO item
	) {
		// Load wallet
		Wallet wallet = walletService.findOne(walletId, getAuthenticated(userService).getId());
		
		// Build item data and persist
		WalletItem walletItem = item.toEntity();
		walletItem.setWallet(wallet);
		walletItem = service.save(walletItem);
		
		return responseAsCreated(walletItem.getId());
	}

	@GetMapping("/page")
	public ResponseEntity<Object> getPage(
		@PathVariable(name = "walletId") Long walletId,
		@RequestParam(name = "page", defaultValue = "0") int pageIndex,
		@RequestParam(name = "startDate", required = true) String dateFrom,
		@RequestParam(name = "endDate", required = true) String dateTo
	) {
		try {
			// Security: allow only the owner access
			walletService.isOwnerOrThrow(walletId, getAuthenticatedId(userService));
			
			// Load wallet items
			DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			Page<WalletItem> items = service.findBetweenDates(walletId, fmt.parse(dateFrom), fmt.parse(dateTo), pageIndex);
			Page<WalletItemDTO> dtos = items.map(i -> new WalletItemDTO(i));
			return ResponseEntity.ok(dtos);
		} catch (Exception e) {
			return ResponseEntity.status(500).build();
		}

	}

	@GetMapping("/type")
	public ResponseEntity<List<WalletItemDTO>> getByType(
		@PathVariable(name = "walletId") Long walletId,
		@RequestParam(name = "type", required = true) WalletItemType type
	) {
		logger.info("Searching by wallet {} and type {}", walletId, type);
		// Security: allow only the owner access
		walletService.isOwnerOrThrow(walletId, getAuthenticatedId(userService));
					
		// Load items
		List<WalletItem> items = service.findByWalletAndType(walletId, type);
		List<WalletItemDTO> dtos = items.stream().map(i -> new WalletItemDTO(i)).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}
	
	@GetMapping("/total")
	public ResponseEntity<BigDecimal> getSumByWallet(
		@PathVariable(name = "walletId") Long walletId
	) {
		// Security: allow only the owner access
		walletService.isOwnerOrThrow(walletId, getAuthenticatedId(userService));
		
		// Calculate total
		BigDecimal total = service.sumTotalValueByWallet(walletId);
		return ResponseEntity.ok(total);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Void> update(
		@PathVariable(name = "walletId") Long walletId,
		@PathVariable(name = "id") Long id,
		@RequestBody @Valid WalletItemDTO data
	) {
		// Security: allow only the owner access
		walletService.isOwnerOrThrow(walletId, getAuthenticatedId(userService));
		
		// Update item and response client
		service.update(data.toEntity());
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(
		@PathVariable(name = "walletId") Long walletId,
		@PathVariable(name = "id") Long id
	) {
		// Security: allow only the owner access
		walletService.isOwnerOrThrow(walletId, getAuthenticatedId(userService));
		
		// Delete item
		service.deleteOne(id);
		return ResponseEntity.noContent().build();
	}
}
