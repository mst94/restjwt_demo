package de.demo.restjwtdemo.controller;

import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.persistence.PersistenceServiceIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private PersistenceServiceIF persistenceService;

    @PostMapping("/")
    public void createUser(@RequestBody UserModel user, HttpServletResponse response) {
        try {
            persistenceService.createUser(user);
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error appeared");
        }
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateUserById(@PathVariable String id) {
        // ToDo: Implement method
        return "user updated";
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
        try  {
            persistenceService.deleteUserById(id);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
        }
    }

}
