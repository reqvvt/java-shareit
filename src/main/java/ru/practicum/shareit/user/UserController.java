package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Вызван метод getAllUsers() в UserController");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        log.info("Вызван метод getUserById() в UserController");
        return userService.getUserById(userId);
    }

    @PostMapping
    public User addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Вызван метод addUser() в UserController");
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Вызван метод updateUser() в UserController");
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> removeUser(@PathVariable Long userId) {
        log.info("Вызван метод removeUser() в UserController");
        userService.removeUser(userId);
        return ResponseEntity.ok().build();
    }
}




