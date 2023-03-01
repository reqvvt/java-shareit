package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.markers.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Integer bookingId,
                                             @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("getBooking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestParam(defaultValue = "ALL", required = false) String state,
                                              @RequestHeader(X_SHARER_USER_ID) Integer bookerId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + state));
        log.info("getBookings {}, bookingState={}, from={}, size={}", bookerId, bookingState, from, size);
        return bookingClient.getBookings(bookerId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestParam(defaultValue = "ALL", required = false) String state,
                                                  @RequestHeader(X_SHARER_USER_ID) Integer userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + state));
        log.info("getAllByOwnerId {}, bookingState={}, from={}, size={}", userId, bookingState, from, size);
        return bookingClient.getAllByOwnerId(userId, bookingState, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@Validated(Update.class) @RequestBody BookItemRequestDto requestDto,
                                           @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("bookItem {}, requestDto={}", userId, requestDto);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> confirmation(@PathVariable Integer bookingId,
                                               @RequestHeader(X_SHARER_USER_ID) Integer userId,
                                               @RequestParam Boolean approved) {
        log.info("confirmation {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.confirmation(bookingId, userId, approved);
    }
}
