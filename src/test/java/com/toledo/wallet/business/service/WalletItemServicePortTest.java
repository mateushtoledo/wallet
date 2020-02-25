package com.toledo.wallet.business.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.business.domain.WalletItem;
import com.toledo.wallet.business.domain.enums.WalletItemType;
import com.toledo.wallet.system.ports.inbound.WalletItemServicePort;
import com.toledo.wallet.system.ports.outbound.WalletItemRepository;
import com.toledo.wallet.system.ports.outbound.WalletRepository;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class WalletItemServicePortTest {
	@MockBean
	private WalletItemRepository repository;
	@MockBean
	private WalletRepository walletRepository;
	@Autowired
	private WalletItemServicePort service;

	private static final Date DATE = new Date();
	private static final WalletItemType TYPE = WalletItemType.IN;
	private static final String DESCRIPTION = "Conta de água";
	private static final BigDecimal VALUE = BigDecimal.valueOf(65.0);

	@Test
	public void testSave() {
		BDDMockito.given(repository.save(Mockito.any(WalletItem.class))).willReturn(getWalletItemMock());
		BDDMockito.given(walletRepository.findById(Mockito.any(Long.class))).willReturn(Optional.of(new Wallet()));
		WalletItem wi = service.save(getWalletItemMock());

		assertNotNull(wi);
		assertEquals(wi.getDescription(), DESCRIPTION);
		assertEquals(wi.getDescription(), DESCRIPTION);
		assertEquals(wi.getValue().compareTo(VALUE), 0);
	}

	@Test
	public void testFindBetweenDates() {
		List<WalletItem> list = new ArrayList<>();
		list.add(getWalletItemMock());
		Page<WalletItem> page = new PageImpl<>(list);

		BDDMockito
			.given(repository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(Mockito.anyLong(),
					Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(PageRequest.class)))
			.willReturn(page);

		Page<WalletItem> response = service.findBetweenDates(1L, new Date(), new Date(), 0);

		assertNotNull(response);
		assertEquals(response.getContent().size(), 1);
		assertEquals(response.getContent().get(0).getDescription(), DESCRIPTION);
	}

	@Test
	public void testFindByType() {
		// Mock repository response
		BDDMockito.given(repository.findByWalletIdAndType(Mockito.anyLong(), Mockito.any(WalletItemType.class))).willReturn(Arrays.asList(getWalletItemMock()));

		// Call service method
		List<WalletItem> response = service.findByWalletAndType(1L, WalletItemType.IN);

		// Validate service response
		assertNotNull(response);
		assertEquals(response.get(0).getDate(), DATE);
		assertEquals(response.get(0).getDescription(), DESCRIPTION);
		assertEquals(response.get(0).getType(), TYPE);
		assertEquals(response.get(0).getValue(), VALUE);
	}

	@Test
	public void testSumByWallet() {
		// Define the expected value
		BigDecimal value = BigDecimal.valueOf(45);
		
		// Mock repository and call service method
		BDDMockito.given(repository.totalItemsValueByWalletId(Mockito.anyLong())).willReturn(value);
		BigDecimal response = service.sumTotalValueByWallet(1L);
		
		// Check if the service not changed the value
		assertEquals(response.compareTo(value), 0);
	}

	private WalletItem getWalletItemMock() {
		return new WalletItem(1L, new Wallet(1L, null, null, null), DATE, TYPE, DESCRIPTION, VALUE);
	}
}
