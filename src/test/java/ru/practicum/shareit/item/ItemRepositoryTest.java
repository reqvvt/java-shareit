package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    User user;
    Item item;
    ItemRequest itemRequest;

    @BeforeEach
    void init() {
        user = userRepository.save(new User(1, "userName", "user@mail.ru"));
        itemRequest = itemRequestRepository.save(new ItemRequest(1, "req", user, LocalDateTime.now()));
        item = itemRepository.save(new Item(1, "item", "descr", true, user.getId(), itemRequest));
    }

    @Test
    void search() {
        Page<Item> res = itemRepository.search("item", Pageable.unpaged());

        assertNotNull(res);
        assertEquals(item, res.stream().findFirst().get());
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        Page<Item> res = itemRepository.findAllByOwnerIdOrderByIdAsc(user.getId(), Pageable.unpaged());

        assertNotNull(res);
        assertEquals(1, res.getTotalElements());
    }

    @Test
    void findItemByItemRequestId() {
        List<Item> res = itemRepository.findItemByItemRequestId(itemRequest.getId());

        assertNotNull(res);
        assertEquals(1, res.size());
    }
}

