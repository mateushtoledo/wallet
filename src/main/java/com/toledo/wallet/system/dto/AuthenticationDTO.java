package com.toledo.wallet.system.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationDTO implements Serializable {
	private static final long serialVersionUID = -6655135679028356578L;
	
	@NotNull(message = "E-mail omitido!")
	@Email(message = "E-mail em formato inválido!")
	private String email;
	
	@NotNull(message = "Senha omitida!")
	@Length(min = 6, message = "Senha muito curta!")
	private String password;
}
