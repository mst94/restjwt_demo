package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.Token;
import org.springframework.stereotype.Component;

@Component
class TokenUtilJwtImpl implements TokenUtilIF {
    @Override
    public Token generateToken() {
        return null;
    }
}
