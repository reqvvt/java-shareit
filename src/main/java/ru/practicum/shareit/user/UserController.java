package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Вызван метод getAllUsers() в UserController");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        log.info("Вызван метод getUserById() в UserController");
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Вызван метод addUser() в UserController");
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {
        log.info("Вызван метод updateUser() в UserController");
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> removeUser(@PathVariable Integer userId) {
        log.info("Вызван метод removeUser() в UserController");
        userService.removeUser(userId);
        return ResponseEntity.ok().build();
    }
}




