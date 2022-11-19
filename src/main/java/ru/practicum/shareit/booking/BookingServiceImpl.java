package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.MessageFailedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<BookingDto> getAllByBookerId(Integer bookerId, String state) {
        validateState(state);
        userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", bookerId)));
        Set<Booking> bookings = new HashSet<>(bookingRepository.findAllByBookerId(bookerId));
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирования отсутствуют.");
        } else {
            log.info("Получены все бронирования пользователя с id = {} (getAllByBookerId())", bookerId);
            return filterByState(bookings, BookingState.valueOf(state));
        }
    }

    @Override
    public List<BookingDto> getAllByOwnerId(Integer ownerId, String state) {
        validateState(state);
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", ownerId)));
        Set<Booking> bookings = new HashSet<>(bookingRepository.findAllByOwnerId(ownerId));
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирования отсутствуют.");
        } else {
            log.info("Получены все бронирования пользователя с id = {} (getAllByOwnerId())", ownerId);
            return filterByState(bookings, BookingState.valueOf(state));
        }
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Запрос с id = %s не найден", bookingId)));
        Item item = itemRepository.findById(booking.getItem().getId()).get();
        if (booking.getBooker().getId() == ownerId || item.getOwnerId() == ownerId) {
            log.info("Найден запрос с id = {} (getBookingById())", booking.getId());
            return BookingMapper.toBookingDto(booking);
        }
        throw new NotFoundException("Получить данные о бронировании может автор бронирования или владелец вещи");
    }

    @Override
    @Transactional
    public BookingDto updateBooking(BookingDtoIn bookingDtoIn, Integer ownerId) {
        User booker = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", ownerId)));
        Item item = itemRepository.findById(bookingDtoIn.getItemId()).orElseThrow(() -> new NotFoundException(
                String.format("Вещь с id = %s не найдена", bookingDtoIn.getItemId())));
        BookingDto bookingDto = BookingMapper.toBookingDto(bookingDtoIn, item);
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания брони раньше даты начала");
        }
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
        if (booker.getId() == item.getOwnerId()) {
            throw new NotFoundException(String.format("Вы являетесь владельцем вещи с id = %s", item.getId()));
        }
        if (item.getAvailable()) {
            Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
            booking.setStatus(BookingStatus.WAITING);
            log.info("Запрос с id = {} сохранен (save())", booking.getId());
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
        throw new ValidationException(String.format("Вещь с id = %s недоступна для аренды", bookingDto.getItem().getId()));
    }

    @Override
    @Transactional
    public BookingDto confirmation(Integer bookingId, Integer ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Запрос с id = %s не найден", bookingId)));
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", ownerId)));
        itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException(
                String.format("Вещь с id = %s не найдена", booking.getItem().getId())));

        if (booking.getItem().getOwnerId() == ownerId) {
            if (booking.getStatus() == BookingStatus.WAITING) {
                booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            } else {
                throw new ValidationException("Изменение статуса бронирования недоступно");
            }
            log.info("Статус бронированиня у запроса с id = {} изменен на {} (confirmation())", booking.getId(),
                    booking.getStatus());
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
        throw new NotFoundException("Статус брони может изменять только владелец");
    }

    private void validateBooking(BookingDto bookingDto) {
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getStart() == null) {
            throw new ValidationException("Некорректная дата начала брони");
        } else if (bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getEnd() == null) {
            throw new ValidationException("Некорректная дата окончания брони");
        } else if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания брони не может быть раньше даты начала брони");
        }
    }

    private void validateState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new MessageFailedException(String.format("Unknown state: %s", state));
        }
    }

    private List<BookingDto> filterByState(Set<Booking> bookings, BookingState state) {
        List<BookingDto> bookingList = null;
        switch (state) {
            case ALL:
                bookingList = bookings.stream()
                                      .map(BookingMapper::toBookingDto)
                                      .collect(Collectors.toList());
                break;
            case WAITING:
                bookingList = bookings.stream()
                                      .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                                      .map(BookingMapper::toBookingDto)
                                      .collect(Collectors.toList());
                break;
            case REJECTED:
                bookingList = bookings.stream()
                                      .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                                      .map(BookingMapper::toBookingDto)
                                      .collect(Collectors.toList());
                break;
            case PAST:
                bookingList = bookings.stream()
                                      .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                                      .map(BookingMapper::toBookingDto)
                                      .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingList = bookings.stream()
                                      .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                                      .map(BookingMapper::toBookingDto)
                                      .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingList = bookings.stream()
                                      .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                                              booking.getEnd().isAfter(LocalDateTime.now()))
                                      .map(BookingMapper::toBookingDto)
                                      .collect(Collectors.toList());
                break;
        }
        return bookingList;
    }

}
