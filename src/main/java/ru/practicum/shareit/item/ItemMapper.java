package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable());
    }

    public static ItemDtoInfo toItemDtoInfo(Item item) {
        return new ItemDtoInfo(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }
}
