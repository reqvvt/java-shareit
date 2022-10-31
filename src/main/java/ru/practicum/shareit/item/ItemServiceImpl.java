package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public List<Item> getAllItems(Long userId) {
        User owner = userService.getUserById(userId);
        return itemRepository.getAllItems(owner);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId)
                                  .orElseThrow(() -> new NotFoundException((String.format(
                                          "Вещь с itemId = %s не найден", itemId))));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User owner = userService.getUserById(userId);
        Item item = itemRepository.addItem(itemMapper.toItem(itemDto), owner);
        return itemMapper.toItemDto(item);

    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        User owner = userService.getUserById(userId);
        Item oldItem = itemRepository.getItemById(itemId).get();
        if (oldItem.getOwner() == owner) {
            Item item = itemRepository.updateItem(itemMapper.toItem(itemDto), oldItem);
            return itemMapper.toItemDto(item);
        }
        throw new NotFoundException("Редактировать вещь может только владелец");
    }

    @Override
    public void removeItem(Long itemId) {
        ItemDto item = getItemById(itemId);
        itemRepository.removeItem(itemMapper.toItem(item));
    }

    @Override
    public List<Item> searchItem(String text, Long userId) {
        userService.getUserById(userId);
        List<Item> itemsList = new ArrayList<>();
        if (text.length() != 0) {
            itemsList = itemRepository.search(text);
        }
        return itemsList;
    }
}
