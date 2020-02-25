package com.toledo.wallet.system.exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationErrorException extends RuntimeException {
	private static final long serialVersionUID = 6883232599449576543L;
	private List<String> validationErrors = new ArrayList<>();

	public ValidationErrorException(List<String> validationErrors) {
		super();
		this.validationErrors = validationErrors;
	}
}
