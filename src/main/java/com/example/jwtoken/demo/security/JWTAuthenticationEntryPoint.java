package com.example.jwtoken.demo.security;

import com.example.jwtoken.demo.common.errorHandling.ErrorResponse;
import com.example.jwtoken.demo.enumeration.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper;

    @Autowired
    public JWTAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e)
            throws IOException, ServletException {
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        this.mapper.writeValue(httpServletResponse.getWriter(),
                ErrorResponse.of("Sorry! Authentication was not possible",
                        ErrorCode.AUTHENTICATION,
                        HttpStatus.UNAUTHORIZED));
    }
}
