package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingDtoForItem {
    private Integer id;
    private Integer bookerId;
}