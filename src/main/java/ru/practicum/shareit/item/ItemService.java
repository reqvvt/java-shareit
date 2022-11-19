package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemDtoInfo> getAllItems(Integer ownerId);

    ItemDtoInfo getItemById(Integer itemId, Integer ownerId);

    ItemDto addItem(ItemDto itemDto, Integer ownerId);

    ItemDto updateItem(ItemDto itemDto, Integer ownerId, Integer itemId);

    void removeItem(Integer itemId);

    List<ItemDto> searchItem(String text, Integer ownerId);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
