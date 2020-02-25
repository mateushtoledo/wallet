package com.toledo.wallet.system.ports.outbound;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.toledo.wallet.business.domain.WalletItem;
import com.toledo.wallet.business.domain.enums.WalletItemType;
import com.toledo.wallet.system.projections.WalletItemKey;

@Repository
@Transactional(readOnly = true)
public interface WalletItemRepository extends JpaRepository<WalletItem, Long> {

	Page<WalletItem> findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(Long walletId, Date dateFrom, Date dateTo, Pageable pageable);

	List<WalletItem> findByWalletIdAndType(Long walletId, WalletItemType type);

	@Query("SELECT SUM(wi.value) FROM WalletItem wi WHERE wi.wallet.id = :walletId")
	BigDecimal totalItemsValueByWalletId(@Param("walletId") Long walletId);

	@Query("SELECT wi.id AS walletItemId, wi.wallet.id AS walletId, wi.wallet.user.id AS walletUserId FROM WalletItem wi WHERE wi.id = :id")
	Optional<WalletItemKey> projectItemIdAndWalletIdAndUserId(@Param("id") Long id);
}
