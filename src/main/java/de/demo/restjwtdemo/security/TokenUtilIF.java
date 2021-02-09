package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.Token;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface TokenUtilIF {
    Token generateToken(final UserDetails userDetails);
    boolean validateToken(final Token token);
    String getUsernameFromToken(final Token token);
    Claims getClaimsFromToken(final Token token);
    List<GrantedAuthority> getRolesFromToken(final Token token);
}
