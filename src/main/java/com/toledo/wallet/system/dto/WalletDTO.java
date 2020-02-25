package com.toledo.wallet.system.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toledo.wallet.business.domain.Wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletDTO implements Serializable {
	private static final long serialVersionUID = 143378240481890104L;

	private Long id;
	
	@NotNull(message = "Nome omitido.")
	@Length(min=3, message = "O nome deve ter 3 ou mais caracteres.")
	private String name;
	
	@NotNull(message = "Valor omitido.")
	private BigDecimal value;
	
	public Wallet toEntity() {
		return new Wallet(id, name, value, null);
	}
	
	public WalletDTO(Wallet entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.value = entity.getValue();
	}
}
