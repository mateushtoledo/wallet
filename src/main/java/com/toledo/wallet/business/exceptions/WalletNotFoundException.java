package com.toledo.wallet.business.exceptions;

public class WalletNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -4283361426055563160L;

	public WalletNotFoundException(String errorDescription) {
		super(errorDescription);
	}
}
