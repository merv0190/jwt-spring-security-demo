package com.example.jwtoken.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt")
public class JWTTestController {
    @GetMapping
    public String testJWT() {
        return "Hello";
    }

    @PostMapping(value = "/csrf")
    public String testJWTPost() {
        return "POST SUCCESS";
    }

    @GetMapping(value = "/public")
    public String testJWTPUBLIC() {
        return "public success";
    }

    @GetMapping(value = "/admin")
    public String testJWTADMIN() {
        return "admin success";
    }
}
