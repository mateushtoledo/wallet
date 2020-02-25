package com.toledo.wallet.system.ports.inbound;

import com.toledo.wallet.business.domain.Wallet;

public interface WalletServicePort {
	Wallet save(Wallet wlt);
	
	Wallet findOne(Long id, Long userId);
	
	void isOwnerOrThrow(Long walletId, Long userId);
}
