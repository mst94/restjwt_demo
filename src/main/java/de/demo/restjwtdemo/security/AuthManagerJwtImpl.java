package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.LoginCredentials;
import org.springframework.stereotype.Component;

// jwt implementation of the auth manager
@Component
class AuthManagerJwtImpl implements AuthManagerIF {

    // check user credentials for validity and return true if they are valid and false if not
    @Override
    public boolean checkCredentialsValid(final LoginCredentials creds) {
        return false;
    }
}
