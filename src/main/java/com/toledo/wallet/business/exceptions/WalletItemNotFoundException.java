package com.toledo.wallet.business.exceptions;

public class WalletItemNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 707924265060327287L;

	public WalletItemNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
