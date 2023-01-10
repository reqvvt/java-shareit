package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    List<BookingDto> getAllByBookerId(Integer bookerId, String state);

    List<BookingDto> getAllByOwnerId(Integer ownerId, String state);

    BookingDto getBookingById(Integer bookingId, Integer ownerId);

    BookingDto updateBooking(BookingDtoIn bookingDtoIn, Integer ownerId);

    BookingDto confirmation(Integer bookingId, Integer ownerId, Boolean approved);


}
