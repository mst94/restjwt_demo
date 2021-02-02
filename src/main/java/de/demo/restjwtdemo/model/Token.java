package de.demo.restjwtdemo.model;

public abstract class Token {
    private String token;

    public Token(String token)  {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
