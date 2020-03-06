package com.toledo.wallet.system.ports.outbound;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.business.domain.enums.UserRole;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository repository;
	@Autowired
	private BCryptPasswordEncoder encoder;
	private static final String USER_EMAIL = "email.test@toledo.com";
	
	/**
	 * Executed before all tests.
	 */
	@Before
	public void setUp() {
		User roberval = new User();
		roberval.setName("Roberval de Itaquera");
		roberval.setEmail(USER_EMAIL);
		roberval.setPassword(encoder.encode("123456"));
		repository.save(roberval);
	}
	
	/**
	 * Executed after all tests.
	 */
	@After
	public void tearDown() {
		repository.deleteAll();
	}
	
	@Test
	public void testSave() {
		User carlos = new User();
		carlos.setName("Carlos Ferreira");
		carlos.setEmail("carlosferreira@wallet.net");
		carlos.setPassword(encoder.encode("123456"));
		
		User repositoryUser = repository.save(carlos);
		assertNotNull(repositoryUser);
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void testSaveWithSameEmail() {
		// Save Dracula with the same e-mail of the first created user
		User dracula = new User(null, "Lord Dracula", USER_EMAIL, encoder.encode("123456"), new ArrayList<>(), UserRole.ROLE_ADMIN);
		repository.save(dracula);
	}
	
	@Test
	public void testFindByEmail() {
		Optional<User> dbUser = repository.findByEmailEquals(USER_EMAIL);
		assertTrue(dbUser.isPresent());
		assertEquals(dbUser.get().getEmail(), USER_EMAIL);
	}
	
	@Test
	public void testFindIdByEmail() {
		Optional<Long> dbId = repository.findIdByEmail(USER_EMAIL);
		assertTrue(dbId.isPresent());
	}
	
	@Test
	public void testNotFoundIdByEmail() {
		Optional<Long> dbId = repository.findIdByEmail("skqwiufhyerbfgeftrfg65dg");
		assertFalse(dbId.isPresent());
	}
}
