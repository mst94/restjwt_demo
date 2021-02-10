package de.demo.restjwtdemo.controller;

import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.persistence.PersistenceServiceIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private PersistenceServiceIF persistenceService;


    @PostMapping("/")
    public void createUser(@Valid @RequestBody UserModel user, HttpServletResponse response) {
        try {
            persistenceService.createUser(user.trimAll());
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error appeared");
        }
    }

    @PutMapping("/{id}")
    public void updateUserById(@PathVariable int id, @Valid @RequestBody UserModel user, HttpServletResponse response) {
        try {
            persistenceService.updateUserById(id, user.trimAll());
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error appeared");
        }
    }

    @GetMapping("/{id}")
    public UserModel readUserById(@PathVariable int id, HttpServletResponse response) {
        UserModel user;
        try {
            user = persistenceService.getUserById(id);
            response.setStatus(HttpStatus.OK.value());
            return user;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable int id) {
        try {
            // prevent user for deleting herself
            User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserModel user = persistenceService.getUserByUsername(loggedInUser.getUsername());
            if (user.getId() == id) {
                throw new BadCredentialsException("Error: you cannot delete" +
                        "yourself!");
            }
            // delete user from db
            persistenceService.deleteUserById(id);
        } catch (BadCredentialsException b) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot delete yourself!");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
        }
    }

}
