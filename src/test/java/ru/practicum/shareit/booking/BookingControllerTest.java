package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    User booker;
    User owner;
    Item item;
    BookingDto bookingDto;
    BookingDtoIn bookingDtoIn;

    @BeforeEach
    void init() {
        booker = new User(1, "testName", "test@mail.ru");
        owner = new User(2, "testName2", "test2@mail.ru");
        item = new Item(1, "item", "descrItem", true, owner.getId(), null);
        bookingDto = new BookingDto(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);
        bookingDtoIn = new BookingDtoIn(bookingDto.getItem().getId(), bookingDto.getStart(), bookingDto.getEnd());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyInt(), anyInt()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(bookingDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
               .andExpect(jsonPath("$.item", is(bookingDto.getItem()), Item.class))
               .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), User.class));

        verify(bookingService, times(1))
                .getBookingById(anyInt(), anyInt());
    }

    @Test
    void getAllByBookerId() throws Exception {
        when(bookingService.getAllByBookerId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                       .header("X-Sharer-User-Id", booker.getId())
                       .param("state", "ALL")
                       .param("from", "0")
                       .param("size", "5")
                       .content(mapper.writeValueAsString(bookingDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Integer.class))
               .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
               .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));


        verify(bookingService, times(1))
                .getAllByBookerId(anyInt(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getAllByOwnerId() throws Exception {
        when(bookingService.getAllByOwnerId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                       .header("X-Sharer-User-Id", owner.getId())
                       .param("state", "ALL")
                       .param("from", "0")
                       .param("size", "5")
                       .content(mapper.writeValueAsString(bookingDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Integer.class))
               .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
               .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

        verify(bookingService, times(1))
                .getAllByOwnerId(anyInt(), anyString(), anyInt(), anyInt());
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.updateBooking(any(BookingDtoIn.class), anyInt()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(bookingDtoIn))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
               .andExpect(jsonPath("$.item", is(bookingDto.getItem()), Item.class))
               .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), User.class));

        verify(bookingService, times(1))
                .updateBooking(any(BookingDtoIn.class), anyInt());
    }

    @Test
    void confirmation() throws Exception {
        when(bookingService.confirmation(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                       .header("X-Sharer-User-Id", "1")
                       .content(mapper.writeValueAsString(bookingDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON)
                       .param("approved", "true"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
               .andExpect(jsonPath("$.item", is(bookingDto.getItem()), Item.class))
               .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), User.class))
               .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingService, times(1))
                .confirmation(anyInt(), anyInt(), anyBoolean());
    }
}
