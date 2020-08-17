package com.spring.security.jwt.authorization;

import com.spring.security.jwt.authorization.model.AuthenticationRequest;
import com.spring.security.jwt.authorization.model.AuthenticationResponse;
import com.spring.security.jwt.authorization.util.SpringJwtAuthorizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringJwtAuthorizationResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SpringJpaUserDetailsService springJpaUserDetailsService;

    @Autowired
    private SpringJwtAuthorizationUtil springJwtAuthorizationUtil;

    @GetMapping("/")
    public String home() {
        return "<h1>Welcome to the homeland</h1>";
    }

    @GetMapping("/user")
    public String user() {
        return "<h1>Welcome to the User page</h1>";
    }

    @GetMapping("/admin")
    public String admin() {
        return "<h1>Welcome to the Admin page</h1>";
    }

    /**
     * This method leverages UserDetailsService to authenticate users using username and password
     * if successful authentication, using userdetails, jwt is created and returned in the response
     *
     * Users will not be required to be authenticated prior to calling this endpoint
     * so we need to let spring security to know to permitAll request when this
     * endpoint is called
     *
     * @param request
     * @return response
     * @throws Exception
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) throws Exception {

        try {
            // Authenticate Request using JPA, This is a standard token spring security uses for username and password
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
        } catch (BadCredentialsException bce) {
            throw new Exception("Incorrect username or password", bce);
        }

        final UserDetails userDetails = springJpaUserDetailsService.loadUserByUsername(request.getUserName());

        final String jwt = springJwtAuthorizationUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
}
