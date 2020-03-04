package com.toledo.wallet.system.adapters.inbound;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toledo.wallet.system.dto.AuthenticationDTO;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class AuthenticationRestResourceTest extends AuthenticatedTestBase {
	private static final String URL = "/auth";
	
	@Autowired
	private MockMvc mvc;
	
	@Before
	public void setUp() {
		mockAuthentication();
	}
	
	@Test
	public void testSucessAuthentication() throws Exception {
		String requestBody = getAuthenticationRequestJson(EXPECTED_EMAIL, EXPECTED_PASSWORD);
		
		// Run authentication request
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(requestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isOk())
		.andExpect(header().exists(AUTHORIZATION_HEADER_NAME));
	}
	
	@Test
	public void testEmailNotExists() throws Exception {
		String requestBody = getAuthenticationRequestJson("randomemail@to.error.net", EXPECTED_PASSWORD);
		
		// Run authentication request
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(requestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testInvalidPassword() throws Exception {
		String requestBody = getAuthenticationRequestJson(EXPECTED_EMAIL, "ewughwcyrjcf");
		
		// Run authentication request
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(requestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testInvalidEmailFormat() throws Exception {
		String requestBody = getAuthenticationRequestJson("teste", EXPECTED_PASSWORD);
		
		// Run authentication request
		// (expected 400 status code and the message defined in the 'email' field of AuthenticationDTO)
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(requestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors[0]").value("E-mail em formato inválido!"));
	}
	
	@Test
	public void testShortPassword() throws Exception {
		String requestBody = getAuthenticationRequestJson(EXPECTED_EMAIL, "123");
		
		// Run authentication request
		// (expected 400 status code and the message defined in the 'password' field of AuthenticationDTO)
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(requestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors[0]").value("Senha muito curta!"));
	}
	
	@Test
	public void testRefreshTokenAuthenticated() throws Exception {
		// Try to refresh token
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL + "/refresh")
				.content("".getBytes())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(status().isOk())
		.andExpect(header().exists(AUTHORIZATION_HEADER_NAME))
		.andExpect(header().string(AUTHORIZATION_HEADER_NAME, Matchers.startsWith(AUTHORIZATION_HEADER_PREFIX)));
	}
	
	@Test
	public void testRefreshTokenUnauthenticated() throws Exception {
		// Call user resource
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL + "/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isUnauthorized());
	}
	
	private String getAuthenticationRequestJson(String email, String password) {
		try {
			AuthenticationDTO dto = new AuthenticationDTO(email, password);
			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(dto);
		} catch (Exception e) {
			return null;
		}
	}
}
