package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.LoginCredentials;

public interface AuthManagerIF {
    boolean checkCredentialsValid(final LoginCredentials creds);
}
