package com.toledo.wallet.system.ports.outbound;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.toledo.wallet.business.domain.User;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailEquals(String email);
	
	boolean existsByEmail(String email);

	@Query("SELECT id FROM User WHERE email = :email")
	Optional<Long> findIdByEmail(@Param("email") String email);
}
