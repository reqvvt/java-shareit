package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDtoInfo> getAllItems(Integer ownerId) {
        log.info("Получены все вещи пользователя c id = {} (getAllItems())", ownerId);
        return itemRepository.findAllByOwner(ownerId).stream()
                             .map(i -> toItemDtoInfo(i, ownerId))
                             .collect(Collectors.toList());
    }

    @Override
    public ItemDtoInfo getItemById(Integer itemId, Integer ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Вещь с id = %s не найдена", itemId)));
        log.info("Найдена вещь с id = {} (getItemById())", itemId);
        return toItemDtoInfo(item, ownerId);
    }

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Integer ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь c id = %s не имеет бронирований", ownerId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        log.info("Вещь с id = {} сохранена (addItem())", item.getId());
        return ItemMapper.toItemDto(itemRepository.save(item));

    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Integer ownerId, Integer itemId) {
        userRepository.findById(ownerId);
        Item oldItem = itemRepository.findById(itemId)
                                     .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена", itemId)));
        if (oldItem.getOwnerId().equals(ownerId)) {
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                oldItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                oldItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                oldItem.setAvailable(itemDto.getAvailable());
            }
            log.info("Данные о вещи с id = {} обновлены (updateItem())", oldItem.getId());
            return ItemMapper.toItemDto(itemRepository.save(oldItem));
        }
        throw new NotFoundException("Только владелец может менять информацию о вещи");
    }

    @Override
    @Transactional
    public void removeItem(Integer itemId) {
        itemRepository.deleteById(itemId);
        log.info("Вещь с id = {} удалена", itemId);
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer ownerId) {
        List<ItemDto> listItem = new ArrayList<>();
        if (text.length() != 0) {
            listItem = itemRepository.search(text).stream()
                                     .map(ItemMapper::toItemDto)
                                     .collect(Collectors.toList());
        }
        log.info("Поиск с параметром text = {}", text);
        return listItem;
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Вещь с id = %s не найдена", itemId)));
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));
        Booking booking = bookingRepository.findAllByBookerId(userId).stream()
                                           .filter(b -> b.getItem().getId().equals(itemId))
                                           .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                                           .findFirst()
                                           .orElseThrow(() -> new ValidationException(
                                                   String.format("Пользователь c id = %s не имеет бронирований", userId)));

        if (booking.getBooker().getId().equals(userId)) {
            Comment comment = CommentMapper.toComment(commentDto, item, author, LocalDateTime.now());
            log.info("Комментарий к вещи с id = {} пользователем с id = {} добавлен", itemId, userId);
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        }
        throw new ValidationException("Отзыв может оставить только пользователь, который брал в аренду эту вещь");
    }

    private ItemDtoInfo toItemDtoInfo(Item item, Integer ownerId) {
        Booking lastBooking = bookingRepository.findLastBooking(item.getId(), ownerId)
                                               .orElse(null);
        Booking nextBooking = bookingRepository.findNextBooking(item.getId(), ownerId)
                                               .orElse(null);
        List<CommentDto> commentDtos = commentRepository.findAllByItemId(item.getId()).stream()
                                                        .map(CommentMapper::toCommentDto)
                                                        .collect(Collectors.toList());
        ItemDtoInfo itemDtoInfo = ItemMapper.toItemDtoInfo(item);
        try {
            itemDtoInfo.setLastBooking(BookingMapper.toBookingDtoForItem(lastBooking));
        } catch (NullPointerException e) {
            e.getMessage();
        }
        try {
            itemDtoInfo.setNextBooking(BookingMapper.toBookingDtoForItem(nextBooking));
        } catch (NullPointerException e) {
            e.getMessage();
        }
        itemDtoInfo.setComments(commentDtos);
        return itemDtoInfo;
    }
}
