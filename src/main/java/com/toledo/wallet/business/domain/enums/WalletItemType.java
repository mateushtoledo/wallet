package com.toledo.wallet.business.domain.enums;

public enum WalletItemType {
	IN("INPUT"),
	OU("OUTPUT");
	
	private final String value;
	
	WalletItemType(String value) {
		this.value = value;
	}
	
	public static WalletItemType toEnum(String value) {
		for (WalletItemType wit : WalletItemType.values()) {
			if (wit.getValue().equals(value)) {
				return wit;
			}
		}
		return null;
	}
	
	public String getValue() {
		return value;
	}
}
