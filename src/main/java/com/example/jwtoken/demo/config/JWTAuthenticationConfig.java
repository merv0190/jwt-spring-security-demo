package com.example.jwtoken.demo.config;

import com.example.jwtoken.demo.enumeration.Role;
import com.example.jwtoken.demo.properties.JWTProperties;
import com.example.jwtoken.demo.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class JWTAuthenticationConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JWTProperties jwtProperties;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JWTValidationFilter jwtValidationFilter;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.customUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        cookieCsrfTokenRepository.setCookiePath("/");
        http
                .csrf().csrfTokenRepository(cookieCsrfTokenRepository)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/jwt/csrf")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/jwt/admin")
                .hasAuthority(Role.ADMIN.toString())
                .antMatchers(HttpMethod.GET, "/jwt/public")
                .hasAuthority(Role.PUBLIC.toString())
                .antMatchers(HttpMethod.GET, "/jwt")
                .hasAnyAuthority(Role.ADMIN.toString(), Role.PUBLIC.toString())
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(this.jwtAuthenticationEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(this.customAccessDeniedHandler)
                .and()
                .logout().deleteCookies(this.jwtProperties.getCookieName())
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
                .and()
                .addFilter(this.jwtAuthenticationFilter)
                .addFilterBefore(this.jwtValidationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
