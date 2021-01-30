package de.demo.restjwtdemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UserController {

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
    @ResponseStatus(HttpStatus.OK)
    public String readUserById(@PathVariable String id) {
        // ToDo: Implement method
        return "test";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable String id) {
        // ToDo: Implement method
    }

}
