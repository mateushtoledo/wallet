package com.toledo.wallet.system.ports.inbound;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import com.toledo.wallet.business.domain.WalletItem;
import com.toledo.wallet.business.domain.enums.WalletItemType;
import com.toledo.wallet.business.exceptions.WalletNotFoundException;

public interface WalletItemServicePort {
	
	WalletItem save(WalletItem item);

	Page<WalletItem> findBetweenDates(long walletId, Date dateFrom, Date dateTo, int pageIndex) throws WalletNotFoundException;

	List<WalletItem> findByWalletAndType(long l, WalletItemType in) throws WalletNotFoundException;

	BigDecimal sumTotalValueByWallet(long walletId) throws WalletNotFoundException;

	WalletItem findOne(Long id) throws WalletNotFoundException;
	
	WalletItem update(WalletItem data) throws WalletNotFoundException;
	
	void deleteOne(Long id) throws WalletNotFoundException;
}
