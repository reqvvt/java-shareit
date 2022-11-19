package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        log.info("Получен список всех пользователей (getAllUsers())");
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserById(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                (String.format("Пользователь с userId = %s не найден", userId))));
        log.info("Получен пользователь с id = {}", userId);
        return UserMapper.toUser(user);
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUserDto(userDto));
        log.info("Пользователь с id = {} создан", user.getId());
        return UserMapper.toUser(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User user = UserMapper.toUserDto(getUserById(userId));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        log.info("Данные пользователя с id = {} обновлены", user.getId());
        User newUser = userRepository.save(user);
        return UserMapper.toUser(newUser);
    }

    @Override
    @Transactional
    public void removeUser(Integer userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удален", userId);
    }
}
