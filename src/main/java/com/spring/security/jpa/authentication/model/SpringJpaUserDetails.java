package com.spring.security.jpa.authentication.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Lets you pass any user name under /user with any username and password = pass
 */
public class SpringJpaUserDetails implements UserDetails {

    private User user;
    private Authority authority;

    public SpringJpaUserDetails(User user, Authority authority) {
        this.user=user;
        this.authority = authority;
    }

    public SpringJpaUserDetails(User user) {
        this.user=user;
    }

    public SpringJpaUserDetails() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Database table has authorities separated by comma
        return Arrays.stream(authority.getAuthority().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
