package de.demo.restjwtdemo.model;

import java.io.Serializable;

// contains credentials of user who makes an login attempt
public class LoginCredentials implements Serializable {
    private String username;
    private String password;    // toDo: hash PW


    //need default constructor for JSON Parsing
    public LoginCredentials()
    { }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
