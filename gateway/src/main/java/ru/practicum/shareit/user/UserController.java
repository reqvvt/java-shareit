package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Вызван метод getAll() в UserController");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Integer userId) {
        log.info("Вызван метод getById() в UserController");
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @NotNull @RequestBody UserDto user) {
        log.info("Вызван метод addUser() в UserController");
        return userClient.create(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> edit(@Valid @NotNull @RequestBody UserDtoUpdate user, @PathVariable Integer userId) {
        log.info("Вызван метод updateUser() в UserController");
        return userClient.edit(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Integer userId) {
        log.info("Вызван метод removeUser() в UserController");
        return userClient.delete(userId);
    }
}




