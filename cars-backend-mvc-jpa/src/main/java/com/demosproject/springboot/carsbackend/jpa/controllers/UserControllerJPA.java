package com.demosproject.springboot.carsbackend.jpa.controllers;

import com.demosproject.springboot.carsbackend.jpa.domain.User;
import com.demosproject.springboot.carsbackend.jpa.dto.UserDto;
import com.demosproject.springboot.carsbackend.jpa.services.UserServiceJPA;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for dealing with user entities.
 */
@RestController
@RequestMapping("/jpa")
public class UserControllerJPA {

    private final UserServiceJPA userServiceJPA;

    public UserControllerJPA(UserServiceJPA userServiceJPA){
        this.userServiceJPA = userServiceJPA;
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getUsers() {
        return userServiceJPA.getUsers();
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getUserById(@PathVariable String id) {
        return userServiceJPA.getUserById(Long.parseLong(id));
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveUser(@PathVariable User user) {
        userServiceJPA.saveUser(user);
    }
}
