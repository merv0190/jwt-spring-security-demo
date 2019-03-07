package com.example.jwtoken.demo.security;

import com.example.jwtoken.demo.model.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomUserDetail extends User {
    private List<Role> roles;

    public CustomUserDetail(String username, String password, List<Role> roles) {
        super(username,
                password,
                true,
                true,
                true,
                true,
                roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toList()));
        this.roles = roles;
    }

    String[] getRolesJWT() {
        return this.roles.stream()
                .map(Role::getName)
                .toArray(String[]::new);
    }
}
