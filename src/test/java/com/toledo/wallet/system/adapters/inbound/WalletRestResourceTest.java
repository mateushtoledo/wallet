package com.toledo.wallet.system.adapters.inbound;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.system.dto.WalletDTO;
import com.toledo.wallet.system.ports.inbound.WalletServicePort;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class WalletRestResourceTest extends AuthenticatedTestBase {
	private static final String URL = "/wallets";
	private static final String WALLET_NAME = "Roberwallet de Itaquera";
	private static final double WALLET_VALUE = 9128.14;
	@MockBean
	private WalletServicePort service;
	@Autowired
	private MockMvc mvc;
	
	@Before
	public void setUp() {
		// Create mock to authentication
		mockAuthentication();
	}
	
	
	@Test
	public void testSave() throws Exception {
		// Create mock to WalletService.save
		BDDMockito
			.given(service.save(Mockito.any(Wallet.class)))
			.willReturn(new Wallet(1827L, WALLET_NAME, BigDecimal.valueOf(WALLET_VALUE), null));
		
		// Call Wallet resource
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(getJsonPayload(null, WALLET_NAME, WALLET_VALUE))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(status().isCreated())
		.andExpect(header().exists("location"));
	}
	
	@Test
	public void testSaveInvalidWalletWithoutName() throws Exception {
		// Call wallet resource
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(getJsonPayload(null, null, 78.5))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(jsonPath("$.errors[0]").value("Nome omitido."))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testSaveInvalidWalletWithoutValue() throws Exception {
		// Call wallet resource
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(getJsonPayload(null, "Água mineral wallet", null))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(jsonPath("$.errors[0]").value("Valor omitido."))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testSaveWalletWithTooShortName() throws Exception {
		// Call wallet resource
		mvc.perform(
			MockMvcRequestBuilders
				.post(URL)
				.content(getJsonPayload(null, "wl", 99.4))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(jsonPath("$.errors[0]").value("O nome deve ter 3 ou mais caracteres."))
		.andExpect(status().isBadRequest());
	}

	public String getJsonPayload(Long id, String name, Double value) throws JsonProcessingException {
		// Create a Wallet DTO
		WalletDTO dto = new WalletDTO(id, name, value == null ? null : BigDecimal.valueOf(value));
		
		// Map wallet DTO to JSON
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dto);
	}
}
