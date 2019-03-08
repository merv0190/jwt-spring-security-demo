package com.example.jwtoken.demo.security;

import com.example.jwtoken.demo.common.exception.InvalidJWTokenException;
import com.example.jwtoken.demo.properties.JWTProperties;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JWTValidationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        Cookie jwtCookie = WebUtils.getCookie(httpServletRequest, this.jwtProperties.getCookieName());
        if (jwtCookie == null || jwtCookie.getValue() == null || StringUtils.isBlank(jwtCookie.getValue())) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            try {
                UsernamePasswordAuthenticationToken authenticationToken = this.getAuthentication(jwtCookie);
                if (authenticationToken == null) {
                    SecurityContextHolder.clearContext();
                } else {
                    authenticationToken.eraseCredentials();
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(context);
                }
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex.getMessage(), ex);
                SecurityContextHolder.clearContext();
                throw ex;
            }
        }
    }

    private Claims validateToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(this.jwtProperties.getSecret().getBytes()))
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature -> Message: {} ", e);
            throw new InvalidJWTokenException("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token -> Message: {}", e);
            throw new InvalidJWTokenException("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token -> Message: {}", e);
            throw new InvalidJWTokenException("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token -> Message: {}", e);
            throw new InvalidJWTokenException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty -> Message: {}", e);
            throw new InvalidJWTokenException("JWT claims string is empty");
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(Cookie jwtCookie) {
        String token = jwtCookie.getValue();
        Claims claims = this.validateToken(token);
        String username = claims.getSubject();
        if (username != null) {
            List<String> roles = claims.get(this.jwtProperties.getRolesPropertyName(), List.class);
            return new UsernamePasswordAuthenticationToken(
                    new CustomUserDetail(username, "", new ArrayList<>()),
                    null,
                    roles == null ? new ArrayList<>() :
                            roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList())
            );
        }
        return null;
    }
}
