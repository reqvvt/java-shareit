package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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
    public List<BookingDto> getAllByBookerId(Integer bookerId, String state, Integer from, Integer size) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("{\"error\": \"Unknown state: " + state + "\" }");
        }
        List<Booking> bookingList;
        LocalDateTime dateTime = LocalDateTime.now();
        userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", bookerId)));
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        bookerId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        bookerId,
                        BookingStatus.REJECTED);
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                        bookerId,
                        dateTime,
                        BookingStatus.APPROVED);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                        bookerId,
                        dateTime);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        bookerId,
                        dateTime,
                        dateTime);
                break;
            default:
                throw new NotFoundException("Недопустимый статус");
        }
        if (bookingList.isEmpty()) {
            throw new NotFoundException("Бронирования отсутствуют.");
        } else {
            log.info("Получены все бронирования пользователя с id = {} (getAllByBookerId())", bookerId);
            return bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingDto> getAllByOwnerId(Integer ownerId, String state, Integer from, Integer size) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("{\"error\": \"Unknown state: " + state + "\" }");
        }
        List<Integer> ownerItemsList = itemRepository.findAllByOwner(ownerId)
                                                     .stream()
                                                     .map(Item::getId)
                                                     .collect(Collectors.toList());
        List<Booking> bookingList;
        LocalDateTime dateTime = LocalDateTime.now();
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", ownerId)));
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findByItemIdInOrderByStartDesc(ownerItemsList);
                break;
            case WAITING:
                bookingList = bookingRepository.findByItemIdInAndStatusEqualsOrderByStartDesc(
                        ownerItemsList,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItemIdInAndStatusEqualsOrderByStartDesc(
                        ownerItemsList,
                        BookingStatus.REJECTED);
                break;
            case PAST:
                bookingList = bookingRepository.findByItemIdInAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
                        ownerItemsList,
                        dateTime,
                        BookingStatus.APPROVED);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItemIdInAndStartIsAfterOrderByStartDesc(
                        ownerItemsList,
                        dateTime);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByItemIdInAndStartBeforeAndEndIsAfterOrderByStartDesc(
                        ownerItemsList,
                        dateTime,
                        dateTime);
                break;
            default:
                throw new NotFoundException("Недопустимый статус");
        }
        if (bookingList.isEmpty()) {
            throw new NotFoundException("Бронирования отсутствуют.");
        } else {
            log.info("Получены все бронирования пользователя с id = {} (getAllByOwnerId())", ownerId);
            return bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Запрос с id = %s не найден", bookingId)));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException(
                String.format("Вещь с id = %s не найдена", booking.getItem().getId())));
        if (booking.getBooker().getId().equals(ownerId) || item.getOwnerId().equals(ownerId)) {
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
        if (bookingDto.getStart().isBefore(LocalDateTime.now())
                || bookingDto.getEnd().isBefore(LocalDateTime.now())
                || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Недопустимое время брони");
        }
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
        if (booker.getId().equals(item.getOwnerId())) {
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

        if (booking.getItem().getOwnerId().equals(ownerId)) {
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

    private PageRequest pagination(Integer from, Integer size) {
        Integer page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.by("start").descending());
    }
}
