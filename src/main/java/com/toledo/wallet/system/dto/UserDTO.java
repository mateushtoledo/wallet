package com.toledo.wallet.system.dto;

import java.io.Serializable;
import java.util.ArrayList;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toledo.wallet.business.domain.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO implements Serializable {
	private static final long serialVersionUID = -998113535779779877L;
	
	private Long id;
	
	@NotNull(message = "Nome omitido")
	@Length(min=3, max=64, message = "O nome deve ter de 3 a 64 caracteres.")
	private String name;
	
	@NotNull(message = "E-mail omitido.")
	@Email(message = "E-mail inválido.")
	private String email;
	
	@NotNull(message = "Senha omitida.")
	@Length(min=6, message = "A senha deve conter 6 ou mais caracteres.")
	private String password;
	
	public User toEntity() {
		return new User(id, name, email, password, new ArrayList<>());
	}
	
	public UserDTO(User entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.email = entity.getEmail();
		//this.password = entity.getPassword();
	}
}
