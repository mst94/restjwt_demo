package de.demo.restjwtdemo.controller;

import de.demo.restjwtdemo.model.LoginCredentials;
import de.demo.restjwtdemo.security.AuthManagerIF;
import de.demo.restjwtdemo.security.TokenUtilIF;
import de.demo.restjwtdemo.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
    private AuthManagerIF authManager;
    private TokenUtilIF tokenUtil;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void setAuthManager(final AuthManagerIF authManager) {
        this.authManager = authManager;
    }

    @Autowired
    public void setTokenUtil(final TokenUtilIF tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Autowired
    public void setUserDetailsService(final UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService= userDetailsService;
    }

    // process new authenticate request
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody final LoginCredentials userCredentials, final HttpServletResponse response)
            throws Exception {
        if (authManager.checkCredentialsValid(userCredentials)) {
            // return new token
            final UserDetails userDetails = userDetailsService.loadUserByUsername()
            return ResponseEntity.ok(tokenUtil.generateToken(userDetails));
        } else {
            throw new Exception("Invalid Credentials!");
        }
        // toDo: Implement method
        // renew token if user already has one. If not, check credentials and generate new token
    }
}
