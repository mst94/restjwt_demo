package de.demo.restjwtdemo.model;

public class UserModel {
    public UserModel()  { }

    private int id;
    private String login;
    private String password;
    private String fname;
    private String lname;
    private String email;
    private UserRolesModel roles;

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

    public UserRolesModel getRoles() {
        return roles;
    }

    public void setRoles(UserRolesModel roles) {
        this.roles = roles;
    }
}
