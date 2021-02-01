package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.LoginCredentials;
import org.springframework.stereotype.Component;

@Component
class AuthManagerJwtImpl implements AuthManagerIF {

    // check user credentials for validity and return true if they are valid and false if not
    @Override
    public boolean checkCredentialsValid(LoginCredentials creds) {
        return false;
    }
}
