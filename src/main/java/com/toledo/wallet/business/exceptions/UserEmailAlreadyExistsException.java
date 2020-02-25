package com.toledo.wallet.business.exceptions;

public class UserEmailAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 12043696227911275L;

	public UserEmailAlreadyExistsException(String errorDescription) {
		super(errorDescription);
	}
}
