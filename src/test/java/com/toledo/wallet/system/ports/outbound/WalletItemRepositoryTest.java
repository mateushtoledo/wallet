package com.toledo.wallet.system.ports.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.business.domain.WalletItem;
import com.toledo.wallet.business.domain.enums.UserRole;
import com.toledo.wallet.business.domain.enums.WalletItemType;
import com.toledo.wallet.system.projections.WalletItemKey;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class WalletItemRepositoryTest {
	@Autowired
	private WalletItemRepository repository;
	@Autowired
	private WalletRepository walletRepository;
	@Autowired
	private UserRepository userRepository;
	
	private static final Date DATE = new Date();
	private static final WalletItemType TYPE = WalletItemType.IN;
	private static final String DESCRIPTION = "Conta de luz";
	private static final BigDecimal VALUE = BigDecimal.valueOf(65);
	
	private Long savedWalletId;
	private Long savedWalletItemId;
	
	@Before
	public void setUp() {
		User walletOwner = new User(null, "Peter Pan", "peter.pan@systemtest.net", "8319isuducyd2", new ArrayList<>(), UserRole.ROLE_ADMIN);
		walletOwner = userRepository.save(walletOwner);
		
		Wallet w = walletRepository.save(new Wallet(null, "Test wallet", BigDecimal.valueOf(7127.9), walletOwner));
		savedWalletId = w.getId();
		
		WalletItem wi = repository.save(new WalletItem(null, w, DATE, TYPE, DESCRIPTION, VALUE));
		savedWalletItemId = wi.getId();
	}
	
	/**
	 * Executed after all tests.
	 */
	@After
	public void tearDown() {
		repository.deleteAll();
		walletRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@Test
	public void testSave() {
		User owner = userRepository.save(new User(null, "PPTX", "pptx@tr.net", "123456", new ArrayList<>(), UserRole.ROLE_ADMIN));
		Wallet wlt = walletRepository.save(new Wallet(null, "Carteira 1", BigDecimal.valueOf(83172), owner));
		WalletItem item = new WalletItem(null, wlt, DATE, TYPE, DESCRIPTION, VALUE);
		WalletItem dbWallet = repository.save(item);
		
		assertNotNull(dbWallet);
		assertNotNull(dbWallet.getId());
		
		assertEquals(wlt.getId(), dbWallet.getWallet().getId());
		assertEquals(DATE, dbWallet.getDate());
		assertEquals(TYPE, dbWallet.getType());
		assertEquals(DESCRIPTION, dbWallet.getDescription());
		assertEquals(VALUE, dbWallet.getValue());
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void testSaveInvalidItem() {
		repository.save(new WalletItem(null, null, DATE, TYPE, DESCRIPTION, VALUE));
	}
	
	@Test
	public void testUpdate() {
		WalletItem targetItem = repository.findById(savedWalletItemId).get();
		WalletItemType oldType = targetItem.getType();
		
		WalletItemType newType = WalletItemType.OU.equals(oldType) ? WalletItemType.IN : WalletItemType.OU;
		String newDescription = "I'm of " + newType.getValue() + " type!";
		targetItem.setType(newType);
		targetItem.setDescription(newDescription);
		
		WalletItem updatedItem = repository.save(targetItem);
		WalletItem updatedItemReloaded = repository.findById(updatedItem.getId()).get();
		assertEquals(newType, updatedItem.getType());
		assertEquals(newType, updatedItemReloaded.getType());
		assertEquals(newDescription, updatedItem.getDescription());
		assertEquals(newDescription, updatedItemReloaded.getDescription());
	}
	
	@Test
	public void testDelete() {
		// Save one item
		Wallet wlt = walletRepository.findById(savedWalletId).get();
		WalletItem dbWallet = repository.save(new WalletItem(null, wlt, DATE, TYPE, DESCRIPTION, VALUE));
		
		// Assert that the item is created
		assertTrue(repository.findById(dbWallet.getId()).isPresent());
		
		// Delete the item and assert that it's removed
		repository.deleteById(dbWallet.getId());
		assertFalse(repository.findById(dbWallet.getId()).isPresent());
	}
	
	@Test
	public void testFindBetweenDates() {
		Wallet w = walletRepository.findById(savedWalletId).get();
		
		// Set the date interval
		LocalDateTime ldt = DATE.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		Date currentDatePlusFiveDays = Date.from(ldt.plusDays(5).atZone(ZoneId.systemDefault()).toInstant());
		Date currentDatePlusSixDays = Date.from(ldt.plusDays(6).atZone(ZoneId.systemDefault()).toInstant());
		Date currentDatePlusSevenDays = Date.from(ldt.plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
		
		// Create new wallets in this interval
		List<WalletItem> newItems = Arrays.asList(
			new WalletItem(null, w, currentDatePlusFiveDays, TYPE, DESCRIPTION, VALUE),
			new WalletItem(null, w, currentDatePlusSixDays, TYPE, DESCRIPTION, VALUE),
			new WalletItem(null, w, currentDatePlusSevenDays, TYPE, DESCRIPTION, VALUE)
		);
		repository.saveAll(newItems);
		
		// Run the query
		PageRequest pg = PageRequest.of(0, 10);
		Page<WalletItem> items = repository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(savedWalletId, currentDatePlusFiveDays, currentDatePlusSevenDays, pg);
		
		// Validate result
		assertEquals(newItems.size(), items.getTotalElements());
		assertEquals(newItems.size(), items.getContent().size());
		items.getContent().forEach(item -> {
			assertEquals(savedWalletId, item.getWallet().getId());
		});
	}
	
	@Test
	public void testFindByType() {
		List<WalletItem> items = repository.findByWalletIdAndType(savedWalletId, TYPE);
		
		// Validate returned item type
		assertEquals(1, items.size());
		assertEquals(TYPE, items.get(0).getType());
	}
	
	@Test
	public void testFindByOUType() {
		Wallet w = walletRepository.findById(savedWalletId).get();
		
		// Save a wallet with type OU and do query
		repository.save(new WalletItem(null, w, DATE, WalletItemType.OU, DESCRIPTION, VALUE));
		List<WalletItem> items = repository.findByWalletIdAndType(savedWalletId, WalletItemType.OU);
		
		assertEquals(1, items.size());
		assertEquals(WalletItemType.OU, items.get(0).getType());
	}
	
	@Test
	public void testSumByWallet() {
		// Save a new item with a random value
		Wallet w = walletRepository.findById(savedWalletId).get();
		BigDecimal randomValue = new BigDecimal(Math.random());
		repository.save(new WalletItem(null, w, DATE, WalletItemType.OU, DESCRIPTION, randomValue));
		
		// Calculate the expected value (with two decimal places, to match with the database sum)
		BigDecimal expectedValue = (VALUE.add(randomValue)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		// Calculate the sum from database items
		BigDecimal dbSum = repository.totalItemsValueByWalletId(savedWalletId);
		// Compare the sums
		assertEquals(0, expectedValue.compareTo(dbSum));
	}
	
	@Test
	public void testProjectItemIdAndWalletIdAndUserId() {
		// Save a new item with a random value
		Wallet w = walletRepository.findById(savedWalletId).get();
		BigDecimal randomValue = new BigDecimal(Math.random());
		WalletItem item = repository.save(new WalletItem(null, w, DATE, WalletItemType.OU, DESCRIPTION, randomValue));
		
		Optional<WalletItemKey> dbKey = repository.projectItemIdAndWalletIdAndUserId(item.getId());
		assertNotNull(dbKey.get());
		
		assertEquals(item.getId(), dbKey.get().getWalletItemId());
		assertEquals(item.getWallet().getId(), dbKey.get().getWalletId());
		assertEquals(item.getWallet().getUser().getId(), dbKey.get().getWalletUserId());
	}
	
	@Test
	public void testProjectItemNotFound() {
		// Save a new item and delete it to assert if it not exists
		Wallet w = walletRepository.findById(savedWalletId).get();
		Long itemId = repository.save(new WalletItem(null, w, DATE, WalletItemType.OU, DESCRIPTION, VALUE)).getId();
		repository.deleteById(itemId);
		
		Optional<WalletItemKey> dbKey = repository.projectItemIdAndWalletIdAndUserId(itemId);
		assertFalse(dbKey.isPresent());
	}
}
