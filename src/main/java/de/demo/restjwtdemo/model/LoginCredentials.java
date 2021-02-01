package de.demo.restjwtdemo.model;

// contains credentials of user who makes an login attempt
public class LoginCredentials {
    private String username;
    private String password;    // toDo: hash PW

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
