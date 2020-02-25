package com.toledo.wallet.system.ports.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.toledo.wallet.business.domain.Wallet;

@Repository
@Transactional(readOnly = true)
public interface WalletRepository extends JpaRepository<Wallet, Long> {
	boolean existsByIdAndUserId(Long id, Long userId);
}
