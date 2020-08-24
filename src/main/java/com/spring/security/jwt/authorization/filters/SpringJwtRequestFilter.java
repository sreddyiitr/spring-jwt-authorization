package com.spring.security.jwt.authorization.filters;

import com.spring.security.jwt.authorization.SpringJwtUserDetailsService;
import com.spring.security.jwt.authorization.util.SpringJwtAuthorizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SpringJwtRequestFilter extends OncePerRequestFilter {

    /**
     * To help with validating the user validation in the header
     */
    @Autowired
    private SpringJwtUserDetailsService springJwtUserDetailsService;

    /**
     * To help with validating jwt token in the authorization header
     */
    @Autowired
    private SpringJwtAuthorizationUtil springJwtAuthorizationUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String userName = null;

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Leaving "Bearer "
            userName = springJwtAuthorizationUtil.extractUsername(jwt);
        }

        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.springJwtUserDetailsService.loadUserByUsername(userName);

            /**
             * If jwt is valid, put the authentication token into the security context
             */
            if(this.springJwtAuthorizationUtil.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().
                        buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // This is to let filterchain continue to its filtering job
        filterChain.doFilter(request, response);
    }
}
