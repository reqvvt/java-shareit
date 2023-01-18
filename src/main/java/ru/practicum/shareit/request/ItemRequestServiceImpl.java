package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public ItemRequestDto create(Integer userId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester, LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestOutDto> getAll(Integer userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                                    .map(r -> ItemRequestMapper.toItemRequestDtoOut(r, getItems(r.getId())))
                                    .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestOutDto> getAllByOtherUsers(Integer userId, Integer from, Integer size) {
        Integer page = from / size;
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        return itemRequestRepository.findAllByRequesterIdNot(userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"))).stream()
                                    .map(r -> ItemRequestMapper.toItemRequestDtoOut(r, getItems(r.getId())))
                                    .collect(Collectors.toList());
    }

    @Override
    public ItemRequestOutDto getById(Integer userId, Integer requestId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос с id = %s пользователя с id = %s не найден", requestId, userId)));
        return ItemRequestMapper.toItemRequestDtoOut(itemRequest, getItems(requestId));
    }

    private List<ItemDto> getItems(Integer id) {
        return itemRepository.findItemByItemRequestIdOrderByIdDesc(id).stream()
                             .map(ItemMapper::toItemDto)
                             .collect(Collectors.toList());
    }
}
