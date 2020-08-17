package com.spring.security.jpa.authentication;

import com.spring.security.jpa.authentication.model.Authority;
import com.spring.security.jpa.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringJpaAuthorityRepository extends JpaRepository<Authority, String> {

    Optional<Authority> findByUserName(String userName);
}
