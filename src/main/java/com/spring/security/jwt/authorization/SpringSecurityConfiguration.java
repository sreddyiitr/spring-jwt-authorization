package com.spring.security.jwt.authorization;

import com.spring.security.jwt.authorization.filters.SpringJwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * Configure spring security to have custom authentication & authorization
 */

@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    //Added to validate token
    @Autowired
    SpringJwtRequestFilter springJwtRequestFilter;

    /**
     * Authentication Using JPA
     * Spring doesn't provide out of the box implementation using JPA for authentication
     * In order for Spring to work with JPA, we need to create UserDetailsService which
     * in turn use JPA to talk to the database. Technicaly speaking, UserDetailsService
     * can just read the data from anywhere and doesn't have to be using JPA necessarily
     *
     * Refer to SpringAuthenticationJpa under images tab
     *
     * Spring calls the userDetailsService whenever there is an authentication attempt
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService);
    }

    /**
     * Authorization
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //cross site request forgery is disabled. No form login :)
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/authenticate").permitAll()
                .anyRequest().authenticated()
                // Spring security doesn't need to create session because we are taking over
                // by validating jwt in the header
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Since we are doing the validation using jwt, we need to make sure set token in the security context
        http.addFilterBefore(springJwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Spring security expects us to pass password encoder
     * @return
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        //Because we are saving passwords in clear text
        return NoOpPasswordEncoder.getInstance();
    }
}
