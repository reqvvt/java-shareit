package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOut> getAll(Integer userId);

    List<ItemRequestDtoOut> getAllByOtherUsers(Integer userId, Integer from, Integer size);

    ItemRequestDtoOut getById(Integer userId, Integer requestId);
}
