package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestOutDto> getAll(Integer userId);

    List<ItemRequestOutDto> getAllByOtherUsers(Integer userId, Integer from, Integer size);

    ItemRequestOutDto getById(Integer userId, Integer requestId);
}
