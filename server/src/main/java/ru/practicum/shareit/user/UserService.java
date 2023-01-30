package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Integer userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Integer userId);

    void removeUser(Integer userId);
}
