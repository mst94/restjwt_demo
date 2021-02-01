package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.LoginCredentials;

public interface AuthManagerIF {
    boolean checkCredentialsValid(LoginCredentials creds);
}
