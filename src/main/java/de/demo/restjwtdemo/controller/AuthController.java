package de.demo.restjwtdemo.controller;

import de.demo.restjwtdemo.model.LoginCredentials;
import de.demo.restjwtdemo.persistence.PersistenceServiceIF;
import de.demo.restjwtdemo.security.TokenUtilIF;
import de.demo.restjwtdemo.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.servlet.tags.form.PasswordInputTag;

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class AuthController {
    private AuthenticationManager authManager;
    private TokenUtilIF tokenUtil;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void setAuthManager(final AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Autowired
    public void setTokenUtil(final TokenUtilIF tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Autowired
    private PersistenceServiceIF persistenceServiceIF;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    public void setUserDetailsService(final UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // process new authenticate request
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody final LoginCredentials userCredentials, final HttpServletResponse response)
            throws Exception {
        try {
            System.out.println("hello user: " + userCredentials.getUsername());
            authManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentials.getUsername(),
                    userCredentials.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        // generate and return new token
        // it is intended that user who already has a token is getting also a new one
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userCredentials.getUsername());
        return ResponseEntity.ok(tokenUtil.generateToken(userDetails));
    }
}
