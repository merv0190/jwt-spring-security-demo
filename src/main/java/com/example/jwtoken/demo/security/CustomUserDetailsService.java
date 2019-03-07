package com.example.jwtoken.demo.security;

import com.example.jwtoken.demo.model.Person;
import com.example.jwtoken.demo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private PersonService personService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = this.personService.findPersonByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(""));
        return new CustomUserDetail(person.getUsername(), person.getPassword(),
                new ArrayList<>(person.getRoles()));
    }
}
