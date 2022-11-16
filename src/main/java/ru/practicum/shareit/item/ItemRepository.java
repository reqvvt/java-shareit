package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long itemId = 0L;

    public List<Item> getAllItems(User owner) {
        return items.values()
                    .stream()
                    .filter(item -> item.getOwner().equals(owner))
                    .collect(Collectors.toList());
    }

    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public Item addItem(Item item, User owner) {
        item.setId(updateItemId());
        item.setOwner(owner);
        items.put(item.getId(), item);
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
        items.remove(item.getId());
    }

    public List<Item> search(String text) {
        String str = text.toLowerCase(Locale.ROOT);
        Set<Item> itemsByName = items.values()
                                     .stream()
                                     .filter(Item::getAvailable)
                                     .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(str))
                                     .collect(Collectors.toSet());
        Set<Item> itemsByDescription = items.values()
                                            .stream()
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
