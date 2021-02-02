package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.LoginCredentials;

// basic interface for an authentication manager
public interface AuthManagerIF {
    boolean checkCredentialsValid(final LoginCredentials creds);
}
