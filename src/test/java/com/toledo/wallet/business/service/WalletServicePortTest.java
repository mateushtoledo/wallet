package com.toledo.wallet.business.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.business.domain.enums.UserRole;
import com.toledo.wallet.business.exceptions.WalletAcessByNotOwnerException;
import com.toledo.wallet.business.exceptions.WalletNotFoundException;
import com.toledo.wallet.system.ports.inbound.WalletServicePort;
import com.toledo.wallet.system.ports.outbound.WalletRepository;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class WalletServicePortTest {
	
	@MockBean
	private WalletRepository repository;
	@Autowired
	private WalletServicePort service;
	private static final Long VALID_ID = 1L;
	private static final Long INVALID_ID = 2L;
	private static final User WALLET_OWNER = new User(VALID_ID, "Wallet Owner", "owner@walletsys.net", "243412", null, UserRole.ROLE_ADMIN);
	
	@Before
	public void setUp() {
		// Mock save method of repository
		BDDMockito.given(repository.save(Mockito.any(Wallet.class)))
			.willReturn(getWalletMock());
		
		// Mock repository.findOne
		Mockito.when(repository.findById(Mockito.eq(VALID_ID)))
			.thenReturn(Optional.of(getWalletMock()));
		Mockito.when(repository.findById(Mockito.eq(INVALID_ID)))
			.thenReturn(Optional.empty());
	
		// Mock repository.existsById
		Mockito.when(repository.existsById(Mockito.eq(VALID_ID)))
			.thenReturn(true);
		Mockito.when(repository.existsById(Mockito.eq(INVALID_ID)))
			.thenReturn(false);
		
		// Mock repository.existsByIdAndUserId
		Mockito.when(repository.existsByIdAndUserId(Mockito.eq(VALID_ID), Mockito.eq(VALID_ID)))
			.thenReturn(true);
		Mockito.when(repository.existsByIdAndUserId(Mockito.eq(INVALID_ID), Mockito.eq(VALID_ID)))
			.thenReturn(false);
		Mockito.when(repository.existsByIdAndUserId(Mockito.eq(VALID_ID), Mockito.eq(INVALID_ID)))
			.thenReturn(false);
	}

	@Test
	public void save() {
		Wallet target = new Wallet(null, "Roberwallet", new BigDecimal(8127.09), WALLET_OWNER);
		target = service.save(target);
		
		assertNotNull(target);
		assertNotNull(target.getId());
	}
	
	@Test
	public void testIsOwner() {
		service.isOwnerOrThrow(VALID_ID, VALID_ID);
		assertTrue(true);
	}
	
	@Test(expected = WalletNotFoundException.class)
	public void testIsOwnerAndNotFoundWallet() {
		service.isOwnerOrThrow(INVALID_ID, VALID_ID);
	}
	
	@Test(expected = WalletAcessByNotOwnerException.class)
	public void testIsNotOwner() {
		service.isOwnerOrThrow(VALID_ID, INVALID_ID);
	}
	
	@Test(expected = WalletNotFoundException.class)
	public void testIsNotOwnerAndWalletNotExists() {
		service.isOwnerOrThrow(INVALID_ID, INVALID_ID);
	}
	
	@Test
	public void testFindById() {
		Wallet dbWallet = service.findOne(VALID_ID, VALID_ID);
		assertNotNull(dbWallet);
		assertNotNull(dbWallet.getId());
	}
	
	@Test(expected = WalletAcessByNotOwnerException.class)
	public void testFindByIdAndNotOwner() {
		Wallet dbWallet = service.findOne(VALID_ID, INVALID_ID);
		assertNotNull(dbWallet);
		assertNotNull(dbWallet.getId());
	}
	
	@Test(expected = WalletNotFoundException.class)
	public void testNotFoundById() {
		Wallet dbWallet = service.findOne(INVALID_ID, VALID_ID);
		assertNull(dbWallet);
	}
	
	public Wallet getWalletMock() {
		return new Wallet(VALID_ID, "Roberwallet", new BigDecimal(8127.09), WALLET_OWNER);
	}
}
