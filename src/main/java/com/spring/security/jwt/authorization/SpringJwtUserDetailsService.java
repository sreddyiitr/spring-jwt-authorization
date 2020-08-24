package com.spring.security.jwt.authorization;

import com.spring.security.jpa.authentication.SpringJpaAuthorityRepository;
import com.spring.security.jpa.authentication.SpringJpaUserRepository;
import com.spring.security.jpa.authentication.model.Authority;
import com.spring.security.jpa.authentication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.spring.security.jpa.authentication.model.SpringJpaUserDetails;

import java.util.Optional;

@Service
public class SpringJwtUserDetailsService implements UserDetailsService {

    @Autowired
    SpringJpaUserRepository userRepository;

    @Autowired
    SpringJpaAuthorityRepository authorityRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        /** You can just return UserDetails from this method by reading
         * credentials from anywhere or hardcoding

         return new SpringJpaHardCodedUserDetails(userName); */

        Optional<User> user = userRepository.findByUserName(userName);
        Optional<Authority> authority = authorityRepository.findByUserName(userName);
        if(user.isPresent() && authority.isPresent()) {
            return new SpringJpaUserDetails(user.get(), authority.get());
        } else if(user.isPresent()) {
            return new SpringJpaUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("User name not found: " + userName);
        }
    }
}
