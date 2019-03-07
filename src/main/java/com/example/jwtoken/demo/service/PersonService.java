package com.example.jwtoken.demo.service;

import com.example.jwtoken.demo.model.Person;
import com.example.jwtoken.demo.model.Role;
import com.example.jwtoken.demo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Transactional(readOnly = true)
    public Optional<Person> findPersonByUsername(String username) {
        return this.personRepository.findByUsername(username);
    }

    @Transactional
    public void insertData() {
        BCryptPasswordEncoder bCryptPasswordEncoder =
                new BCryptPasswordEncoder();
        Person personAllRoles = Person.builder()
                .username("user")
                .password(bCryptPasswordEncoder.encode("hello"))
                .build();
        Person admin = Person.builder()
                .username("admin")
                .password(bCryptPasswordEncoder.encode("admin"))
                .build();
        Person personPublic = Person.builder()
                .username("public")
                .password(bCryptPasswordEncoder.encode("public"))
                .build();
        Role roleAdmin = Role.builder()
                .name("ADMIN")
                .build();
        Role rolePublic = Role.builder()
                .name("PUBLIC")
                .build();
        personAllRoles.getRoles().add(roleAdmin);
        personAllRoles.getRoles().add(rolePublic);
        admin.getRoles().add(roleAdmin);
        personPublic.getRoles().add(rolePublic);
        this.personRepository.save(personAllRoles);
        this.personRepository.save(admin);
        this.personRepository.save(personPublic);
    }
}
