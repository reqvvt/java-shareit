package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "testName", "test@mail.ru");
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
               .andExpect(status().isOk())
               .andExpect(content().json("[]"));

        verify(userService, times(1))
                .getAllUsers();
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyInt()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userDto.getId())
                       .content(mapper.writeValueAsString(userDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
               .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
               .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

        verify(userService, times(1))
                .getUserById(anyInt());
    }

    @Test
    void addUser() throws Exception {
        when(userService.addUser(any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                       .content(mapper.writeValueAsString(userDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
               .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
               .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

        verify(userService, times(1))
                .addUser(any(UserDto.class));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any(UserDto.class), anyInt()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", userDto.getId())
                       .content(mapper.writeValueAsString(userDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
               .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
               .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

        verify(userService, times(1))
                .updateUser(any(UserDto.class), anyInt());
    }

    @Test
    void removeUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userDto.getId())
                       .content(mapper.writeValueAsString(userDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        verify(userService, times(1))
                .removeUser(anyInt());
    }
}
