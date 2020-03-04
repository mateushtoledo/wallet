package com.toledo.wallet.business.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.After;
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
import com.toledo.wallet.business.exceptions.UserEmailAlreadyExistsException;
import com.toledo.wallet.business.exceptions.UserNotFoundException;
import com.toledo.wallet.system.ports.inbound.UserServicePort;
import com.toledo.wallet.system.ports.outbound.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserServicePortTest {
	@MockBean
	private UserRepository repository;
	@Autowired
	private UserServicePort service;
	
	@Before
	public void setUp() {
		// Mock save user (of user repository)
		BDDMockito.given(repository.save(Mockito.any(User.class)))
			.willReturn(getUserMock());
	}
	
	@After
	public void tearDown() {
		repository.deleteAll();
	}
	
	@Test
	public void testSave() {
		// Mock exists by email (of user repository)
		BDDMockito.given(repository.existsByEmail(Mockito.anyString()))
			.willReturn(false);
		
		User morganFreeman = new User(null, "Morgan Freeman", "morganfreeman@actor.net", "i_am_freeman", new ArrayList<>());
		morganFreeman = service.save(morganFreeman);
		
		assertNotNull(morganFreeman);
		assertNotNull(morganFreeman.getId());
	}
	
	@Test(expected = UserEmailAlreadyExistsException.class)
	public void testSaveUserWithUsedEmail() {
		// Mock exists by email (of user repository)
		BDDMockito.given(repository.existsByEmail(Mockito.anyString()))
			.willReturn(true);
		
		// Save user
		User morganFreeman = new User(null, "Morgan Freeman", "morganfreeman@actor.net", "i_am_freeman", new ArrayList<>());
		morganFreeman = service.save(morganFreeman);
	}
	
	@Test
	public void testFindByEmail() {
		// Mock find by email equals (of user repository)
		BDDMockito.given(repository.findByEmailEquals(Mockito.anyString()))
			.willReturn(Optional.of(new User()));
				
		User user = service.findByEmail("email.test@toledo.com");
		assertNotNull(user);
	}
	
	@Test(expected = UserNotFoundException.class)
	public void testFindByInvalidEmail() {
		// Mock find by email equals (of user repository)
		String testEmail = "email22.test@toledo.com";
		doReturn(Optional.empty()).when(repository).findByEmailEquals(Mockito.eq(testEmail));
				
		service.findByEmail(testEmail);
	}
	
	@Test
	public void testFindIdByEmail() {
		// Mock find by email equals (of user repository)
		BDDMockito.given(repository.findIdByEmail(Mockito.anyString()))
			.willReturn(Optional.of(1L));
		
		Long userId = service.getIdByEmail("email.test@toledo.com");
		assertNotNull(userId);
	}
	
	@Test(expected = UserNotFoundException.class)
	public void testNotFoundIdByEmail() {
		// Mock find by email equals (of user repository)
		BDDMockito.given(repository.findIdByEmail(Mockito.anyString()))
			.willReturn(Optional.empty());
		
		service.getIdByEmail("email.test@toledo.com");
	}
	
	public User getUserMock() {
		return new User(1L, "Morgan Freeman", "morganfreeman@actor.net", "i_am_freeman", new ArrayList<>());
	}
}
