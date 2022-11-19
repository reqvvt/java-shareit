package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String X_SHARER_USER_ID = "X_Sharer-User-Id";

    @GetMapping("{bookingId}")
    public BookingDto getBookingById(@RequestHeader(X_SHARER_USER_ID) Integer ownerId, @PathVariable Integer bookingId) {
        log.info("Вызван метод getBookingById() в BookingController");
        return bookingService.getBookingById(bookingId, ownerId);
    }

    @GetMapping
    public List<BookingDto> getAllByBookerId(@RequestHeader(X_SHARER_USER_ID) Integer bookerId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Вызван метод getAllByBookerId() в BookingController");
        return bookingService.getAllByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByUser(@RequestHeader(X_SHARER_USER_ID) Integer ownerId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Вызван метод getAllByOwnerId() в BookingController");
        return bookingService.getAllByOwnerId(ownerId, state);
    }

    @PostMapping
    public BookingDto updateBooking(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                             @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Вызван метод updateBooking() в BookingController");
        return bookingService.updateBooking(bookingDtoIn, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto confirmation(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                   @PathVariable Integer bookingId,
                                   @RequestParam Boolean approved) {
        log.info("Вызван метод confirmation() в BookingController");
        return bookingService.confirmation(bookingId, userId, approved);
    }
}
