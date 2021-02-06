package de.demo.restjwtdemo.controller;

import de.demo.restjwtdemo.model.User;
import de.demo.restjwtdemo.persistence.JdbcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private JdbcService jdbc;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public String createUser() {
        // ToDo: Implement method
        return "user created";
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateUserById(@PathVariable String id) {
        // ToDo: Implement method
        return "user updated";
    }

    @GetMapping("/{id}")
    public User readUserById(@PathVariable int id, HttpServletResponse response)  {
        User user;
        try {
            user = jdbc.readUserById(id);
            response.setStatus(HttpStatus.OK.value());
            return user;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable String id) {
        // ToDo: Implement method
    }

}
