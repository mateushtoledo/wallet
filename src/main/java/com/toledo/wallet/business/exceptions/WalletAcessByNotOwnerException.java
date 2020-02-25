package com.toledo.wallet.business.exceptions;

public class WalletAcessByNotOwnerException extends RuntimeException {
	private static final long serialVersionUID = -636365455365989561L;

	public WalletAcessByNotOwnerException(String errorMessage) {
		super(errorMessage);
	}
}
