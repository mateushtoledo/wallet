package com.toledo.wallet.system.ports.inbound;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.business.exceptions.UserNotFoundException;

public interface UserServicePort {
	
	User save(User userData);
	
	User findByEmail(String email) throws UserNotFoundException;

	Long getIdByEmail(String email) throws UserNotFoundException;
}
