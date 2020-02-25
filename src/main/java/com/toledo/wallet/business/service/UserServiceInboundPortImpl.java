package com.toledo.wallet.business.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.toledo.wallet.business.domain.User;
import com.toledo.wallet.business.exceptions.UserEmailAlreadyExistsException;
import com.toledo.wallet.business.exceptions.UserNotFoundException;
import com.toledo.wallet.system.ports.inbound.UserServicePort;
import com.toledo.wallet.system.ports.outbound.UserRepository;

@Service
public class UserServiceInboundPortImpl implements UserServicePort {
	@Autowired
	private UserRepository repository;
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Override
	public User save(User userData) {
		// Check if the received e-mail isn't used actually
		if (repository.existsByEmail(userData.getEmail())) {
			throw new UserEmailAlreadyExistsException("O e-mail informado está associado a uma conta do sistema!");
		}
		
		// Encode user password
		String encodedPassword = encoder.encode(userData.getPassword());
		userData.setPassword(encodedPassword);
		
		// Save user
		return repository.save(userData);
	}

	@Override
	@Cacheable(value = "findUserByEmail")
	public User findByEmail(String email) throws UserNotFoundException {
		Optional<User> dbUser = repository.findByEmailEquals(email);
		if (dbUser.isPresent()) {
			return dbUser.get();
		}
		throw new UserNotFoundException("Não existe nenhuma conta com o e-mail informado!");
	}

	@Override
	@Cacheable(value = "getUserIdByEmail")
	public Long getIdByEmail(String email) throws UserNotFoundException {
		Optional<Long> userId = repository.findIdByEmail(email);
		return userId.orElseThrow(() -> new UserNotFoundException("Usuário não encontrado pelo e-mail!"));
	}
}
