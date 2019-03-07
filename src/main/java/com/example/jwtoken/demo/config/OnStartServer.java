package com.example.jwtoken.demo.config;

import com.example.jwtoken.demo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class OnStartServer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private PersonService personService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        try {
//            this.personService.insertData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
