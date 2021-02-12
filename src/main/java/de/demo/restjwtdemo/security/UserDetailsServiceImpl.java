package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.persistence.PersistenceServiceIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private PersistenceServiceIF persistenceService;

    @Autowired
    public void setPersistenceService(final PersistenceServiceIF persistenceService)  {
        this.persistenceService = persistenceService;
    }

    // fetch user details from data source using username
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {
            // fetch user from database
            UserModel foundUser = persistenceService.getUserByUsername(username.trim());
            return new User(foundUser.getLogin(), foundUser.getPassword(), persistenceService
                    .getRolesOfUserByUserId(foundUser.getId()));
        } catch (Exception e) {
            throw new UsernameNotFoundException("User " + username + " not found!");
        }

    }
}
