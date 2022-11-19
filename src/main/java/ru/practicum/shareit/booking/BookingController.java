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
    public BookingDto getBookingById(@PathVariable Integer bookingId,
                                     @RequestHeader(X_SHARER_USER_ID) Integer ownerId) {
        log.info("Вызван метод getBookingById() в BookingController");
        return bookingService.getBookingById(bookingId, ownerId);
    }

    @GetMapping
    public List<BookingDto> getAllByBookerId(@RequestParam(defaultValue = "ALL", required = false) String state,
                                             @RequestHeader(X_SHARER_USER_ID) Integer bookerId) {
        log.info("Вызван метод getAllByBookerId() в BookingController");
        return bookingService.getAllByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerId(@RequestParam(defaultValue = "ALL", required = false) String state,
                                            @RequestHeader(X_SHARER_USER_ID) Integer ownerId) {
        log.info("Вызван метод getAllByOwnerId() в BookingController");
        return bookingService.getAllByOwnerId(ownerId, state);
    }

    @PostMapping
    public BookingDto updateBooking(@Valid @RequestBody BookingDtoIn bookingDtoIn,
                                    @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("Вызван метод updateBooking() в BookingController");
        return bookingService.updateBooking(bookingDtoIn, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto confirmation(@PathVariable Integer bookingId,
                                   @RequestHeader(X_SHARER_USER_ID) Integer userId,
                                   @RequestParam Boolean approved) {
        log.info("Вызван метод confirmation() в BookingController");
        return bookingService.confirmation(bookingId, userId, approved);
    }
}
