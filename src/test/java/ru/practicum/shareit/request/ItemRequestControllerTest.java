package ru.practicum.shareit.request;

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
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    ItemRequestOutDto itemRequestOutDto;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        itemRequestOutDto = new ItemRequestOutDto(1, "descr", LocalDateTime.now(), null);
        itemRequestDto = new ItemRequestDto(itemRequestOutDto.getId(), itemRequestOutDto.getDescription(),
                itemRequestOutDto.getCreated());
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(anyInt(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(itemRequestDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
               .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .create(anyInt(), any());
    }

    @Test
    void getAllByRequester() throws Exception {
        when(itemRequestService.getAll(anyInt()))
                .thenReturn(List.of(itemRequestOutDto));

        mockMvc.perform(get("/requests")
                       .header("X-Sharer-User-Id", "1")
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
               .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .getAll(anyInt());
    }

    @Test
    void getAllByOtherUsers() throws Exception {
        when(itemRequestService.getAllByOtherUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestOutDto));

        mockMvc.perform(get("/requests/all")
                       .header("X-Sharer-User-Id", "1")
                       .param("from", "0")
                       .param("size", "2")
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
               .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .getAllByOtherUsers(anyInt(), anyInt(), anyInt());
    }

    @Test
    void getById() throws Exception {
        when(itemRequestService.getById(anyInt(), anyInt()))
                .thenReturn(itemRequestOutDto);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                       .header("X-Sharer-User-Id", "1")
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
               .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .getById(anyInt(), anyInt());
    }
}
