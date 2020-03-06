package com.toledo.wallet.system.ports.outbound;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.business.domain.enums.UserRole;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class WalletRepositoryTest {
	@Autowired
	private WalletRepository repository;
	@Autowired
	private UserRepository userRepository;
	private User walletOwner;
	
	@Before
	public void setUp() {
		walletOwner = new User(null, "Wallet owner", "kingofwallets@wallet.net", "123456", new ArrayList<>(), UserRole.ROLE_ADMIN);
		walletOwner = userRepository.save(walletOwner);
	}
	
	/**
	 * Executed after all tests.
	 */
	@After
	public void tearDown() {
		repository.deleteAll();
		userRepository.deleteAll();
	}
	
	@Test
	public void testSave() {
		Wallet toSave = new Wallet(null, "Roberwallet", new BigDecimal(7463.10), walletOwner);
		Wallet dbWallet = repository.save(toSave);
		
		assertNotNull(dbWallet);
		assertNotNull(dbWallet.getId());
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void testSaveWithoutUser() {
		Wallet toSave = new Wallet(null, "Roberwallet", new BigDecimal(7463.10), null);
		repository.save(toSave);
	}
	
	@Test
	public void testExistsByIdAndUser() {
		Wallet userWallet = new Wallet(null, "Roberwallet", new BigDecimal(7463.10), walletOwner);
		userWallet = repository.save(userWallet);
		
		boolean exists = repository.existsByIdAndUserId(userWallet.getId(), walletOwner.getId());
		assertTrue(exists);
	}
	
	@Test
	public void testNotExistsByIdAndUser() {
		Wallet userWallet = new Wallet(null, "Roberwallet", new BigDecimal(7463.10), walletOwner);
		userWallet = repository.save(userWallet);
		
		boolean existsWithInvalidWalletId = repository.existsByIdAndUserId(0L, walletOwner.getId());
		boolean existsWithInvalidUserId = repository.existsByIdAndUserId(userWallet.getId(), 0L);
		
		assertFalse(existsWithInvalidWalletId);
		assertFalse(existsWithInvalidUserId);
	}
	
	@Test
	public void testExists() {
		// Save a wallet
		Wallet userWallet = new Wallet(null, "Roberwallet", new BigDecimal(7463.10), walletOwner);
		userWallet = repository.save(userWallet);
		
		// It exists?
		assertTrue(repository.existsById(userWallet.getId()));
	}
	
	@Test
	public void testNotExists() {
		// Save a wallet and delete it
		Wallet wallet = new Wallet(null, "Roberwallet", new BigDecimal(7463.10), walletOwner);
		Long walletId = repository.save(wallet).getId();
		repository.deleteById(walletId);
		
		// It exists?
		assertFalse(repository.existsById(walletId));
	}
}
