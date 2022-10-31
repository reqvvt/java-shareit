package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private List<Item> items = new ArrayList<>();
    private Long itemId = 0L;

    public List<Item> getAllItems(User owner) {
        return items.stream()
                    .filter(item -> item.getOwner() == owner)
                    .collect(Collectors.toList());
    }

    public Optional<Item> getItemById(Long itemId) {
        return items.stream()
                    .filter(item -> item.getId() == itemId)
                    .findAny();
    }

    public Item addItem(Item item, User owner) {
        item.setId(updateItemId());
        item.setOwner(owner);
        items.add(item);
        return item;
    }

    public Item updateItem(Item item, Item oldItem) {
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return oldItem;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> search(String text) {
        String str = text.toLowerCase(Locale.ROOT);
        Set<Item> itemsByName = items.stream()
                                     .filter(Item::getAvailable)
                                     .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(str))
                                     .collect(Collectors.toSet());
        Set<Item> itemsByDescription = items.stream()
                                            .filter(Item::getAvailable)
                                            .filter(item -> item.getDescription().toLowerCase(Locale.ROOT).contains(str))
                                            .collect(Collectors.toSet());
        itemsByName.addAll(itemsByDescription);
        return List.copyOf(itemsByName);
    }

    private Long updateItemId() {
        return ++itemId;
    }
}
