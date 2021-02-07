package de.demo.restjwtdemo.persistence;

import de.demo.restjwtdemo.model.UserModel;

import java.sql.SQLException;

public interface PersistenceServiceIF {
    boolean createUser(UserModel user) throws Exception;
    UserModel getUserById(int id) throws Exception;
    UserModel getUserByUsername(String username) throws Exception;
    boolean updateUserById(int id);
    boolean deleteUserById(int id) throws Exception;
}
