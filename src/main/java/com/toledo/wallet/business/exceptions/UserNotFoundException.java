package com.toledo.wallet.business.exceptions;

public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1434694862554321674L;

	public UserNotFoundException(String errorDescription) {
		super(errorDescription);
	}
	
}
