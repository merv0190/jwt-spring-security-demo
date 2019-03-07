package com.example.jwtoken.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ConfigurationProperties // no prefix, find root level values.
@PropertySource("classpath:jwt.properties")
@Component
@Data
public class JWTProperties {
    private String secret;
    private int expiration;
    private String cookieName;
    private String rolesPropertyName;
}
