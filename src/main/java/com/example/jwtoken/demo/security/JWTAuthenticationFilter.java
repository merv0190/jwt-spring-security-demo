package com.example.jwtoken.demo.security;

import com.example.jwtoken.demo.common.exception.AuthAlreadyAuthenticatedException;
import com.example.jwtoken.demo.model.Person;
import com.example.jwtoken.demo.properties.JWTProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

@Component
@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper mapper;

    @Autowired
    private JWTProperties jwtProperties;

    @Autowired
    public JWTAuthenticationFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    @Autowired
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        super.setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Person person;
            try {
                person = this.mapper.readValue(request.getReader(), Person.class);
                if (person != null && !StringUtils.isBlank(person.getUsername())) {
                    if (!StringUtils.isBlank(person.getPassword())) {
                        UsernamePasswordAuthenticationToken token =
                                new UsernamePasswordAuthenticationToken(person.getUsername(), person.getPassword());
                        return this.getAuthenticationManager().authenticate(token);
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            throw new BadCredentialsException("");
        } else {
            throw new AuthAlreadyAuthenticatedException("");
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        try {
            CustomUserDetail authenticatedPerson =
                    (CustomUserDetail) authResult.getPrincipal();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }
            String[] roles = authenticatedPerson.getRolesJWT();
            Claims claims = Jwts.claims()
                    .setSubject(authenticatedPerson.getUsername());
            if (roles.length > 0) {
                claims.put(this.jwtProperties.getRolesPropertyName(), roles);
            }
            String token = Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS512,
                            Base64.getEncoder().encodeToString(this.jwtProperties.getSecret().getBytes()))
                    .compact();

            Cookie jwtCookie = new Cookie(this.jwtProperties.getCookieName(), token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(this.jwtProperties.getExpiration());
            jwtCookie.setPath("/");
            jwtCookie.setSecure(true);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);

            response.addCookie(jwtCookie);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getLocalizedMessage(), ex);
            throw new AuthenticationServiceException("");
        }
    }
}
