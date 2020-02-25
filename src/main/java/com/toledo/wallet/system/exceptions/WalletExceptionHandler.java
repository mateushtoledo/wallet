package com.toledo.wallet.system.exceptions;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.toledo.wallet.business.exceptions.UserEmailAlreadyExistsException;
import com.toledo.wallet.business.exceptions.WalletAcessByNotOwnerException;
import com.toledo.wallet.business.exceptions.WalletItemNotFoundException;
import com.toledo.wallet.business.exceptions.WalletNotFoundException;

@ControllerAdvice
public class WalletExceptionHandler {
	
	@ExceptionHandler(ValidationErrorException.class)
	public ResponseEntity<SystemErrorResponse> handleValidationErrors(ValidationErrorException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new SystemErrorResponse(ex.getValidationErrors()));
	}
	
	@ExceptionHandler(UserEmailAlreadyExistsException.class)
	public ResponseEntity<SystemErrorResponse> handleEmailDuplicate(UserEmailAlreadyExistsException ex) {
		List<String> businessErrors = Arrays.asList(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new SystemErrorResponse(businessErrors));
	}
	
	@ExceptionHandler(WalletNotFoundException.class)
	public ResponseEntity<SystemErrorResponse> handleWalletNotFoundException(WalletNotFoundException ex) {
		List<String> businessErrors = Arrays.asList(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new SystemErrorResponse(businessErrors));
	}
	
	@ExceptionHandler(WalletAcessByNotOwnerException.class)
	public ResponseEntity<SystemErrorResponse> handleWalletAcessByNotOwnerException(WalletAcessByNotOwnerException ex) {
		List<String> businessErrors = Arrays.asList(ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(new SystemErrorResponse(businessErrors));
	}
	
	@ExceptionHandler(WalletItemNotFoundException.class)
	public ResponseEntity<SystemErrorResponse> handleWalletItemNotFoundException(WalletItemNotFoundException ex) {
		List<String> businessErrors = Arrays.asList(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new SystemErrorResponse(businessErrors));
	}
}
