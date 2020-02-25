package com.toledo.wallet.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.toledo.wallet.business.domain.WalletItem;
import com.toledo.wallet.business.domain.enums.WalletItemType;
import com.toledo.wallet.business.exceptions.WalletItemNotFoundException;
import com.toledo.wallet.system.ports.inbound.WalletItemServicePort;
import com.toledo.wallet.system.ports.outbound.WalletItemRepository;

@Service
public class WalletItemServiceInboundPortImpl implements WalletItemServicePort {
	@Autowired
	private WalletItemRepository repository;

	@Override
	@CacheEvict(value = "findItemByWalletAndType", allEntries = true)
	public WalletItem save(WalletItem item) {
		return repository.save(item);
	}

	@Override
	public Page<WalletItem> findBetweenDates(long walletId, Date dateFrom, Date dateTo, int pageIndex) {
		return repository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(walletId, dateFrom, dateTo, PageRequest.of(pageIndex, 10, Sort.by("date")));
	}

	@Override
	@Cacheable(value = "findItemByWalletAndType")
	public List<WalletItem> findByWalletAndType(long walletId, WalletItemType type) {
		return repository.findByWalletIdAndType(walletId, type);
	}

	@Override
	public BigDecimal sumTotalValueByWallet(long walletId) {
		return repository.totalItemsValueByWalletId(walletId);
	}

	@Override
	public WalletItem findOne(Long id) {
		Optional<WalletItem> dbItem = repository.findById(id);
		return dbItem.orElseThrow(() -> new WalletItemNotFoundException("Item não encontrado!"));
	}

	@Override
	@CacheEvict(value = "findItemByWalletAndType", allEntries = true)
	public void deleteOne(Long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
			throw new WalletItemNotFoundException("Item não encontrado!");
		}
	}

	@Override
	@CacheEvict(value = "findItemByWalletAndType", allEntries = true)
	public WalletItem update(WalletItem data) {
		if (repository.existsById(data.getId())) {
			return save(data);
		}
		throw new WalletItemNotFoundException("Item não encontrado!");
	}
}
