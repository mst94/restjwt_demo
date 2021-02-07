package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.persistence.PersistenceServiceSQLImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private PersistenceServiceSQLImpl jdbc;

    // fetch user details from data source using username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            UserModel foundUser = jdbc.getUserByUsername(username);
            return new User(foundUser.getLogin(), foundUser.getPassword(), new ArrayList<>());
        } catch (Exception e) {
            throw new UsernameNotFoundException("User " + username + " not found!");
        }

    }
}
