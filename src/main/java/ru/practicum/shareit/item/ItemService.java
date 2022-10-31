package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<Item> getAllItems(Long userId);
    ItemDto getItemById(Long itemId);
    ItemDto addItem(ItemDto itemDto, Long userId);
    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);
    void removeItem(Long itemId);
    List<Item> searchItem(String text, Long userId);
}
