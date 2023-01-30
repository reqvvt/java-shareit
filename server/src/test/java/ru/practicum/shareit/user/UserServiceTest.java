package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@WebMvcTest(UserService.class)
@AutoConfigureMockMvc
class UserServiceTest {
    @MockBean
    private UserServiceImpl userService;
    private UserRepository userRepository;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void init() {
        userDto = new UserDto(1, "testName", "test@mail.ru");
        user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        final List<UserDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(user.getId());

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
    }

    @Test
    void addUser() {
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto userDto = userService.addUser(this.userDto);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
    }

    @Test
    void updateUser() {
        User userUpdate = new User(1, "updateName", "update@mail.ru");
        UserDto userDtoUp = new UserDto(userUpdate.getId(), userUpdate.getName(), userUpdate.getEmail());
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(userUpdate);

        UserDto res = userService.updateUser(userDtoUp, user.getId());

        assertEquals(userUpdate.getName(), res.getName());
        assertEquals(userUpdate.getEmail(), res.getEmail());
    }

    @Test
    void updateNullNameAndEmail() {
        User userUpdate = new User(1, user.getName(), user.getEmail());
        UserDto userDtoUp = new UserDto(userUpdate.getId(), null, null);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(userUpdate);

        UserDto res = userService.updateUser(userDtoUp, user.getId());

        assertEquals(user.getName(), res.getName());
        assertEquals(user.getEmail(), res.getEmail());
    }

    @Test
    void removeUser() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        userService.removeUser(userDto.getId());
        List<User> users = userRepository.findAll();

        assertEquals(0, users.size());
    }
}
