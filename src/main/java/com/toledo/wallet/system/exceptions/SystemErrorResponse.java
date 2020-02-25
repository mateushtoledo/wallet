package com.toledo.wallet.system.exceptions;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SystemErrorResponse implements Serializable {
	private static final long serialVersionUID = -3427181743814050336L;
	
	private List<String> errors;
	private Long timestamp;
	
	public SystemErrorResponse(List<String> errors) {
		super();
		this.errors = errors;
		this.timestamp = System.currentTimeMillis();
	}
}
