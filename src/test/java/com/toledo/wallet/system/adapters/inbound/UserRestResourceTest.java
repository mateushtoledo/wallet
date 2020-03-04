package com.toledo.wallet.system.adapters.inbound;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.business.exceptions.UserEmailAlreadyExistsException;
import com.toledo.wallet.system.dto.UserDTO;
import com.toledo.wallet.system.ports.inbound.UserServicePort;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserRestResourceTest {
	@MockBean
	private UserServicePort service;
	private static final Long USER_ID = 1L;
	private static final String USER_EMAIL = "email.test@toledo.com";
	private static final String USER_NAME = "User 4 Test";
	private static final String USER_PASSWORD = "123456";
	private static final String URL = "/users";
	
	@Autowired
	private MockMvc mvc;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Test
	public void testSave() throws Exception {
		// Create mock to UserService.save
		BDDMockito
			.given(service.save(Mockito.any(User.class)))
			.willReturn(getMockUser());
		
		// Call user resource
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(getJsonPayload(null, USER_NAME, USER_EMAIL, USER_PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isNotFound())
		.andExpect(header().exists("location"));
	}
	
	@Test
	public void testSaveWithUsedEmail() throws Exception {
		String businessExceptionMessage = "Já existe um usuário com esse e-mail";
		
		// Create mock to UserService.save
		BDDMockito
			.given(service.save(Mockito.any(User.class)))
			.willThrow(new UserEmailAlreadyExistsException(businessExceptionMessage));
		
		// Call user resource
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(getJsonPayload(null, USER_NAME, USER_EMAIL, USER_PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors[0]").value(businessExceptionMessage));
	}
	
	@Test
	public void testSaveInvalidUser() throws Exception {
		// Call user resource
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(getJsonPayload(null, USER_NAME, "email", USER_PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(jsonPath("$.errors[0]").value("E-mail inválido."))
		.andExpect(status().isBadRequest());
	}
	
	public User getMockUser() {
		User u = new User();
		u.setId(USER_ID);
		u.setEmail(USER_EMAIL);
		u.setName(USER_NAME);
		u.setPassword(encoder.encode(USER_PASSWORD));
		return u;
	}
	
	public String getJsonPayload(Long id, String name, String email, String password) throws JsonProcessingException {
		// Create a User DTO
		UserDTO dto = new UserDTO(id, name, email, encoder.encode(password));
		// Map user DTO to JSON
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dto);
	}
}
