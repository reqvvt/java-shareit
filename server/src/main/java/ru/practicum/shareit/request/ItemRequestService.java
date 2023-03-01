package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponse> getAll(Integer userId);

    List<ItemRequestResponse> getAllByOtherUsers(Integer userId, Integer from, Integer size);

    ItemRequestResponse getById(Integer userId, Integer requestId);
}
