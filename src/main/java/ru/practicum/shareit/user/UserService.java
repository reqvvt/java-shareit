package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long userId);
    User addUser(UserDto userDto);
    User updateUser(UserDto userDto, Long userId);
    void removeUser(Long userId);
}
