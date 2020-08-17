package com.spring.security.jpa.authentication;

import com.spring.security.jpa.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringJpaUserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserName(String userName);
}
