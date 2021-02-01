package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.Token;

public interface TokenUtilIF {
    Token generateToken();
}
