package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.JwtToken;
import de.demo.restjwtdemo.model.Token;
import de.demo.restjwtdemo.model.UserData;

public interface TokenUtilIF {
    Token generateToken(final UserData userData);
    boolean checkTokenIsValid(final JwtToken token);
}
