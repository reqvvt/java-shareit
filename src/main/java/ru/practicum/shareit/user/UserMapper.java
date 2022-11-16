package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static User toUserDto(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static UserDto toUser(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
