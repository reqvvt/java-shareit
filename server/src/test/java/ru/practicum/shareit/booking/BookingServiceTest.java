package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@WebMvcTest(BookingService.class)
@AutoConfigureMockMvc
class BookingServiceTest {
    BookingService bookingService;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    Item item;
    User booker;
    User owner;
    Booking booking;
    BookingDto bookingDto;
    BookingDtoIn bookingDtoIn;

    @BeforeEach
    void init() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        booker = new User(1, "booker", "booker@mail.ru");
        owner = new User(2, "owner", "owner@email.ru");
        item = new Item(1, "item", "descrItem", true, owner.getId(), null);
        booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item, booker, BookingStatus.WAITING);
        bookingDto = new BookingDto(1, booking.getStart(), booking.getEnd(), item, booker, booking.getStatus());
        bookingDtoIn = new BookingDtoIn(bookingDto.getItem().getId(), bookingDto.getStart(), bookingDto.getEnd());
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        BookingDto res = bookingService.getBookingById(booking.getId(), owner.getId());

        assertNotNull(res);
        assertEquals(booking.getId(), res.getId());
        assertEquals(booking.getBooker(), res.getBooker());
    }

    @Test
    void getBookingByIdNotFoundException() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        Exception ex = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), owner.getId()));
        assertEquals("Запрос с id = 1 не найден", ex.getMessage());
    }

    @Test
    void getBookingByIdNotFound() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(booking.getId(), owner.getId()));
    }

    @Test
    void confirmation() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto res = bookingService.confirmation(booking.getId(), owner.getId(), true);

        assertNotNull(res);
        assertEquals(booking.getId(), res.getId());
        assertEquals(booking.getItem().getId(), res.getItem().getId());
    }

    @Test
    void confirmationNotFoundBooking() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> bookingService.confirmation(booking.getId(),
                owner.getId(), true));
        assertEquals("Запрос с id = 1 не найден", ex.getMessage());
    }

    @Test
    void confirmationValidateException() {
        booking.setStatus(BookingStatus.CANCELED);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));

        Exception ex = assertThrows(ValidationException.class, () -> bookingService.confirmation(booking.getId(),
                owner.getId(), true));
        assertEquals("Изменение статуса бронирования недоступно", ex.getMessage());
    }

    @Test
    void updateBooking() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto res = bookingService.updateBooking(bookingDtoIn, booker.getId());

        assertNotNull(res);
        assertEquals(booking.getId(), res.getId());
        assertEquals(booking.getItem().getId(), res.getItem().getId());
    }

    @Test
    void updateBookingNotfoundException() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        Exception ex = assertThrows(NotFoundException.class, () -> bookingService.updateBooking(bookingDtoIn, booker.getId()));
        assertEquals("Пользователь с id = 1 не найден", ex.getMessage());
    }

    @Test
    void updateBookingNotfoundExceptionBooker() {
        item.setOwnerId(booker.getId());
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        Exception ex = assertThrows(NotFoundException.class, () -> bookingService.updateBooking(bookingDtoIn, booker.getId()));
        assertEquals("Вы являетесь владельцем вещи с id = 1", ex.getMessage());
    }

    @Test
    void updateBookingValidationExceptionAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        Exception ex = assertThrows(ValidationException.class, () -> bookingService.updateBooking(bookingDtoIn, booker.getId()));
        assertEquals("Вещь с id = 1 недоступна для аренды", ex.getMessage());
    }

    @Test
    void updateBookingValidationException() {
        BookingDtoIn bookingDtoIn1 = new BookingDtoIn(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(2));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.updateBooking(bookingDtoIn1, booker.getId()));
    }
}