package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    ItemDto itemDto;
    Item item;
    User user;
    ItemDtoInfo itemDtoInfo;
    CommentDto commentDto;

    @BeforeEach
    void init() {
        itemDto = new ItemDto(1, "itemName", "descr", true, null);
        user = new User(1, "userName", "user@mail.ru");
        item = new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), true, user.getId(), null);
        itemDtoInfo = new ItemDtoInfo(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), item.getAvailable(),
                null,
                null,
                new ArrayList<>());
        commentDto = new CommentDto(1, "comment", 1, "author",
                LocalDateTime.of(2022, 10, 1, 12, 0, 1));
    }

    @Test
    void getAllItems() throws Exception {
        when(itemService.getAllItems(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoInfo));

        mockMvc.perform(get("/items")
                       .header("X-Sharer-User-Id", "1")
                       .param("from", "0")
                       .param("size", "5"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(itemDtoInfo.getId()), Integer.class))
               .andExpect(jsonPath("$[0].name", is(itemDtoInfo.getName()), String.class))
               .andExpect(jsonPath("$[0].available", is(itemDtoInfo.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .getAllItems(anyInt(), anyInt(), anyInt());
    }

    @Test
    void getAllItemsValidateException() throws Exception {
        mockMvc.perform(get("/items")
                       .header("X-Sharer-User-Id", "1")
                       .param("from", "-1")
                       .param("size", "5")
                       .content(mapper.writeValueAsString(itemDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().is4xxClientError());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyInt(), anyInt()))
                .thenReturn(itemDtoInfo);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(itemDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
               .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
               .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .getItemById(anyInt(), anyInt());
    }

    @Test
    void getItemByIdNotFound() throws Exception {
        when(itemService.getItemById(anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(itemDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().is4xxClientError());
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(itemDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
               .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
               .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .addItem(any(ItemDto.class), anyInt());
    }

    @Test
    void addItemValidateException() throws Exception {
        when(itemService.addItem(any(), anyInt()))
                .thenThrow(ValidationException.class);

        mockMvc.perform(post("/items")
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(itemDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().is4xxClientError());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(), anyInt(), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(itemDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
               .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
               .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .updateItem(any(), anyInt(), anyInt());
    }

    @Test
    void removeItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", itemDto.getId())
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(itemDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        verify(itemService, times(1))
                .removeItem(anyInt());
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItem(anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                       .header("X-Sharer-User-Id", "1")
                       .param("text", "item")
                       .param("from", "0")
                       .param("size", "5")
                       .content(mapper.writeValueAsString(itemDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Integer.class))
               .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
               .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1))
                .searchItem(anyString(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyInt(), anyInt(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(commentDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
               .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
               .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));

        verify(itemService, times(1))
                .addComment(anyInt(), anyInt(), any());
    }
}
