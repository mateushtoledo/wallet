package com.toledo.wallet.business.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.business.exceptions.WalletAcessByNotOwnerException;
import com.toledo.wallet.business.exceptions.WalletNotFoundException;
import com.toledo.wallet.system.ports.inbound.WalletServicePort;
import com.toledo.wallet.system.ports.outbound.WalletRepository;

@Service
public class WalletServiceInboundPortImpl implements WalletServicePort {
	@Autowired
	private WalletRepository repository;

	@Override
	public Wallet save(Wallet wlt) {
		return repository.save(wlt);
	}
	
	@Override
	@Cacheable(value = "findOneWallet")
	public Wallet findOne(Long id, Long userId) {
		Optional<Wallet> wallet = repository.findById(id);
		if (wallet.isPresent()) {
			Wallet found = wallet.get();
			if (found.getUser().getId().equals(userId)) {
				return found;
			}
			throw new WalletAcessByNotOwnerException("Você não possui permissões para visualizar os dados dessa wallet!");
		}
		throw new WalletNotFoundException("Carteira não encontrada!");
	}

	@Override
	@Cacheable(value = "isWalletOwnerOrThrow")
	public void isOwnerOrThrow(Long walletId, Long userId) {
		if (repository.existsById(walletId)) {
			if (!repository.existsByIdAndUserId(walletId, userId)) {
				throw new WalletAcessByNotOwnerException("Você não possui permissões para visualizar os dados dessa wallet!");
			}
		} else {
			throw new WalletNotFoundException("Carteira não encontrada!");
		}
	}
}
