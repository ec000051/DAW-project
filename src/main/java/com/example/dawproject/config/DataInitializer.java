package com.example.dawproject.config;

import com.example.dawproject.dao.UserDAO;
import com.example.dawproject.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class DataInitializer {

    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        userDAO.createSchema();

        if (!userDAO.exists("admin")) {
            userDAO.addUser(new User("admin", "admin", "admin@test.com", "ADMIN"));
        }

        if (!userDAO.exists("user")) {
            userDAO.addUser(new User("user", "user", "user@test.com", "USER"));
        }
    }
}