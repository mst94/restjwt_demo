package de.demo.restjwtdemo.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class UserModel {

    public UserModel() {
    }

    private int id;
    @NotEmpty
    @Size(max = 50)
    private String login;
    @NotEmpty
    @Size(max = 20)
    private String password;
    @NotEmpty
    @Size(max = 50)
    private String fname;
    @NotEmpty
    @Size(max = 50)
    private String lname;
    @NotEmpty
    @Size(max = 100)
    private String email;

    @Autowired(required = false)
    private List<UserRolesEnum> roles = new ArrayList<>();

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UserRolesEnum> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRolesEnum> roles) {
        this.roles = roles;
    }

    public UserModel trimAll() {
        this.setLogin(getLogin().trim());
        this.setEmail(getEmail().trim());
        this.setPassword(getPassword().trim());
        this.setFname(getFname().trim());
        this.setLname(getLname().trim());
        return this;
    }
}
