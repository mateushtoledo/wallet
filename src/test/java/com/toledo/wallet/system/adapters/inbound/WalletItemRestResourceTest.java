package com.toledo.wallet.system.adapters.inbound;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toledo.wallet.business.domain.Wallet;
import com.toledo.wallet.business.domain.WalletItem;
import com.toledo.wallet.business.domain.enums.WalletItemType;
import com.toledo.wallet.business.exceptions.WalletItemNotFoundException;
import com.toledo.wallet.business.exceptions.WalletNotFoundException;
import com.toledo.wallet.system.dto.WalletItemDTO;
import com.toledo.wallet.system.ports.inbound.WalletItemServicePort;
import com.toledo.wallet.system.ports.inbound.WalletServicePort;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WalletItemRestResourceTest extends AuthenticatedTestBase {
	
	@MockBean
	WalletItemServicePort service;
	@MockBean
	WalletServicePort walletService;
	@Autowired
	private MockMvc mvc;
	
	private static final Long VALID_ID = 1L;
	private static final Long INVALID_ID = 2L;
	private static final Date DATE = new Date();
	private static final LocalDate TODAY = LocalDate.now();
	private static final WalletItemType TYPE = WalletItemType.IN;
	private static final String DESCRIPTION = "Conta de Luz";
	private static final BigDecimal VALUE = BigDecimal.valueOf(65);
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String VALID_URL = "/wallets/1/items";
	private static final String INVALID_URL = "/wallets/2/items";
	private static final String WALLET_404_MESSAGE = "Carteira não encontrada!";
	
	@Before
	public void setUp() {
		// Create mock to authentication
		mockAuthentication();
		
		// Mock walletService.findOne
		Mockito.when(walletService.findOne(Mockito.eq(VALID_ID), Mockito.anyLong()))
			.thenReturn(new Wallet(1L, "Wallet", VALUE, null));
		Mockito.when(walletService.findOne(Mockito.eq(INVALID_ID), Mockito.anyLong()))
			.thenThrow(new WalletNotFoundException(WALLET_404_MESSAGE));
	}

	@Test
	public void testSave() throws Exception {
		// Mock service
		BDDMockito.given(service.save(Mockito.any(WalletItem.class))).willReturn(getWalletItemMock());

		// Try to register one item
		mvc.perform(
			MockMvcRequestBuilders.post(VALID_URL)
			.content(getJsonPayload())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)

		// Expected 201 status code and location header
		.andExpect(status().isCreated()).andExpect(header().exists("location"));
	}
	
	@Test
	public void testSaveWithWalletNotFound() throws Exception {
		// Mock service
		BDDMockito.given(service.save(Mockito.any(WalletItem.class))).willReturn(getWalletItemMock());

		// Try to register one item
		mvc.perform(
			MockMvcRequestBuilders.post(INVALID_URL)
			.content(getJsonPayload())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)

		// Expected 404 status code and a message about wallet not found
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.errors[0]").value(WALLET_404_MESSAGE));
	}

	@Test
	public void testFindBetweenDates() throws Exception {
		// Define filters by date of request
		String startDate = TODAY.format(dateFormat);
		String endDate = TODAY.plusDays(5).format(dateFormat);

		// Define service mock response
		List<WalletItem> items = Arrays.asList(getWalletItemMock());
		Page<WalletItem> page = new PageImpl<WalletItem>(items);

		// Create service response mock
		BDDMockito.given(service.findBetweenDates(Mockito.anyLong(), Mockito.any(Date.class), Mockito.any(Date.class),
				Mockito.anyInt())).willReturn(page);

		// Run request to resource
		mvc.perform(
			MockMvcRequestBuilders.get(VALID_URL + "/page?page=1&startDate=" + startDate + "&endDate=" + endDate)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
			)

			// Expected 200 status code
			.andExpect(status().isOk())

			// Expected these values at the received data
			.andExpect(jsonPath("$.content[0].id").value(VALID_ID))
			.andExpect(jsonPath("$.content[0].date").value(TODAY.format(dateFormat)))
			.andExpect(jsonPath("$.content[0].description").value(DESCRIPTION))
			.andExpect(jsonPath("$.content[0].type").value(TYPE.getValue()))
			.andExpect(jsonPath("$.content[0].value").value(VALUE));
	}

	@Test
	public void testFindByType() throws Exception {
		List<WalletItem> list = Arrays.asList(getWalletItemMock());
		BDDMockito.given(service.findByWalletAndType(Mockito.anyLong(), Mockito.any(WalletItemType.class)))
				.willReturn(list);

		mvc.perform(
			MockMvcRequestBuilders.get(VALID_URL + "/type?type=" + TYPE)
			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
			.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id").value(VALID_ID))
		.andExpect(jsonPath("$[0].date").value(TODAY.format(dateFormat)))
		.andExpect(jsonPath("$[0].description").value(DESCRIPTION))
		.andExpect(jsonPath("$[0].type").value(TYPE.getValue())).andExpect(jsonPath("$[0].value").value(VALUE));
	}

	@Test
	public void testSumByWallet() throws Exception {
		BigDecimal value = BigDecimal.valueOf(536.90);
		BDDMockito.given(service.sumTotalValueByWallet(Mockito.anyLong())).willReturn(value);
		
		mvc.perform(
			MockMvcRequestBuilders.get(VALID_URL + "/total")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").value("536.9"));
	}

	@Test
	public void testUpdate() throws Exception {
		// Create service mocks
		WalletItem updatedWallet = new WalletItem(1L, new Wallet(VALID_ID, null, null, null), DATE, WalletItemType.OU, "UPDATE ME", VALUE);
		BDDMockito.given(service.update(Mockito.any(WalletItem.class))).willReturn(updatedWallet);

		// Do the request
		mvc.perform(
			MockMvcRequestBuilders
				.put(VALID_URL + "/99")
				.content(getJsonPayload())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(status().isNoContent());
	}
	
	@Test
	public void testUpdateAndNotFoundWallet() throws Exception {
		Mockito.doThrow(new WalletNotFoundException("exception")).when(walletService).isOwnerOrThrow(Mockito.anyLong(), Mockito.anyLong());
		
		// Do the request
		mvc.perform(
				MockMvcRequestBuilders
					.put(INVALID_URL + "/" + VALID_ID)
					.content(getJsonPayload())
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		)
		.andExpect(status().isNotFound());
	}

	@Test
	public void testDelete() throws JsonProcessingException, Exception {
		BDDMockito.given(service.findOne(Mockito.anyLong())).willReturn(getWalletItemMock());
		mvc.perform(
			MockMvcRequestBuilders.delete(VALID_URL + "/1")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		).andExpect(status().isNoContent());
	}

	@Test
	public void testDeleteItemOfNotFoundWallet() throws Exception {
		Mockito.doThrow(new WalletNotFoundException("exception")).when(walletService).isOwnerOrThrow(Mockito.anyLong(), Mockito.anyLong());
		mvc.perform(
			MockMvcRequestBuilders.delete(INVALID_URL + "/1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		).andExpect(status().isNotFound());
	}
	
	@Test
	public void testDeleteNotFoundItem() throws Exception {
		Mockito.doThrow(new WalletItemNotFoundException("exception")).when(service).deleteOne(Mockito.anyLong());
		
		mvc.perform(
			MockMvcRequestBuilders.delete(VALID_URL + "/" + INVALID_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER_NAME, getAuthorizationToken())
		).andExpect(status().isNotFound());
	}

	private WalletItem getWalletItemMock() {
		return new WalletItem(VALID_ID, new Wallet(VALID_ID, null, null, null), DATE, TYPE, DESCRIPTION, VALUE);
	}

	public String getJsonPayload() throws JsonProcessingException {
		WalletItemDTO dto = new WalletItemDTO(getWalletItemMock());
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dto);
	}
}