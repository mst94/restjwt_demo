package de.demo.restjwtdemo.persistence;

import de.demo.restjwtdemo.model.UserModel;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface PersistenceServiceIF {
    boolean createUser(final UserModel user) throws Exception;
    UserModel getUserById(final int id) throws Exception;
    UserModel getUserByUsername(final String username) throws Exception;
    boolean updateUserById(final int id, final UserModel toUpdate) throws Exception;
    boolean deleteUserById(final int id) throws Exception;
    List<GrantedAuthority> getRolesOfUserByUserId(final int userId) throws Exception;
}
