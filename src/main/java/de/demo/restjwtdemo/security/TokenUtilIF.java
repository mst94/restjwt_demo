package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.JwtToken;
import de.demo.restjwtdemo.model.Token;
import de.demo.restjwtdemo.model.UserData;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenUtilIF {
    Token generateToken(final UserDetails userDetails);
    boolean validateToken(final JwtToken token);
}
