package de.demo.restjwtdemo.model;

// this enums represents the possible roles of a user in the spring security context
// roles must correspond to the certain mysql field names in the role table in order to make matching work successfully
public enum UserRolesEnum {
    ROLE_ADMIN, ROLE_DEVELOP, ROLE_CCTLD, ROLE_GTLD, ROLE_BILLING, ROLE_REGISTRY, ROLE_PURCHASE_READ,
    ROLE_PURCHASE_WRITE, ROLE_SALE_WRITE, ROLE_SQL
}
