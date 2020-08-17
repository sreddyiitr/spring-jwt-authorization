package com.spring.security.jwt.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.spring.security.jpa.authentication"})
@EntityScan(basePackages = {"com.spring.security.jpa.authentication.model"})
@SpringBootApplication
public class SpringJwtAuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJwtAuthorizationApplication.class, args);
    }

}