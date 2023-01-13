package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    List<BookingDto> getAllByBookerId(Integer bookerId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwnerId(Integer ownerId, String state, Integer from, Integer size);

    BookingDto getBookingById(Integer bookingId, Integer ownerId);

    BookingDto updateBooking(BookingDtoIn bookingDtoIn, Integer ownerId);

    BookingDto confirmation(Integer bookingId, Integer ownerId, Boolean approved);


}
