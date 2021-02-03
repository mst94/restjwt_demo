package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.Token;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenUtilIF {
    Token generateToken(final UserDetails userDetails);
    boolean validateToken(final Token token);
    String getUsernameFromToken(final Token token);
}
