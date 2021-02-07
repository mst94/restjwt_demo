package de.demo.restjwtdemo.model;

public class UserRolesModel {
    public UserRolesModel()  {

    }

    private boolean roleAdmin;
    private boolean roleDevelop;
    private boolean roleCCtl;
    private boolean roleGtld;
    private boolean roleBilling;
    private boolean roleRegistry;
    private boolean rolePurchaseRead;
    private boolean rolePurchaseWrite;
    private boolean roleSaleWrite;
    private boolean roleSql;

    public boolean isRoleAdmin() {
        return roleAdmin;
    }

    public void setRoleAdmin(boolean roleAdmin) {
        this.roleAdmin = roleAdmin;
    }

    public boolean isRoleDevelop() {
        return roleDevelop;
    }

    public void setRoleDevelop(boolean roleDevelop) {
        this.roleDevelop = roleDevelop;
    }

    public boolean isRoleCCtl() {
        return roleCCtl;
    }

    public void setRoleCCtl(boolean roleCCtl) {
        this.roleCCtl = roleCCtl;
    }

    public boolean isRoleGtld() {
        return roleGtld;
    }

    public void setRoleGtld(boolean roleGtld) {
        this.roleGtld = roleGtld;
    }

    public boolean isRoleBilling() {
        return roleBilling;
    }

    public void setRoleBilling(boolean roleBilling) {
        this.roleBilling = roleBilling;
    }

    public boolean isRoleRegistry() {
        return roleRegistry;
    }

    public void setRoleRegistry(boolean roleRegistry) {
        this.roleRegistry = roleRegistry;
    }

    public boolean isRolePurchaseRead() {
        return rolePurchaseRead;
    }

    public void setRolePurchaseRead(boolean rolePurchaseRead) {
        this.rolePurchaseRead = rolePurchaseRead;
    }

    public boolean isRolePurchaseWrite() {
        return rolePurchaseWrite;
    }

    public void setRolePurchaseWrite(boolean rolePurchaseWrite) {
        this.rolePurchaseWrite = rolePurchaseWrite;
    }

    public boolean isRoleSaleWrite() {
        return roleSaleWrite;
    }

    public void setRoleSaleWrite(boolean roleSaleWrite) {
        this.roleSaleWrite = roleSaleWrite;
    }

    public boolean isRoleSql() {
        return roleSql;
    }

    public void setRoleSql(boolean roleSql) {
        this.roleSql = roleSql;
    }
}
