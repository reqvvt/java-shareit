package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.getUserById(userId)
                             .orElseThrow(() -> new NotFoundException((String.format(
                                     "Пользователь с userId = %s не найден", userId))));
    }

    @Override
    public User addUser(UserDto userDto) {
        User user = UserMapper.toUserDto(userDto);
        validEmail(user);
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) {
        User user = UserMapper.toUserDto(userDto);
        validEmail(user);
        User oldUser = getUserById(userId);
        return userRepository.updateUser(user, oldUser);
    }

    @Override
    public void removeUser(Long userId) {
        User user = getUserById(userId);
        userRepository.removeUser(user);
    }

    private void validEmail(User user) {
        if (userRepository.contains(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже зарегистрирован");
        }
    }
}
