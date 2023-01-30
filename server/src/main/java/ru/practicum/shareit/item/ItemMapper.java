package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest() == null ? null : item.getItemRequest().getId()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null);
    }

    public static ItemDtoInfo toItemDtoInfo(Item item) {
        return new ItemDtoInfo(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }
}
