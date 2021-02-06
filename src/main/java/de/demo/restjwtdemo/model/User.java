package de.demo.restjwtdemo.model;

public class User {
    private int id;
    private String login;
    public User()  { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
