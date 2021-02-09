package de.demo.restjwtdemo.model;

import java.util.ArrayList;
import java.util.List;

public class SQLRoles  {
    private List<String> roles;

    public SQLRoles()  {
        this.roles = new ArrayList<String>();
        this.roles.add("role_admin");
        this.roles.add("role_sale");
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
