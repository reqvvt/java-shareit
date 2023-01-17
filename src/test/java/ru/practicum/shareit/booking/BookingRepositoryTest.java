package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    User booker;
    User owner;
    Item item;
    Booking booking1;
    Booking lastBooking;
    Booking nextBooking;


    @BeforeEach
    void init() {
        booker = userRepository.save(new User(1, "booker", "booker@mail.ru"));
        owner = userRepository.save(new User(2, "owner", "owner@email.ru"));
        item = itemRepository.save(new Item(1, "item", "descItem", true, owner.getId(), null));
        booking1 = bookingRepository.save(new Booking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), item, booker, BookingStatus.WAITING));
        lastBooking = bookingRepository.save(new Booking(2, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusHours(1), item, booker, BookingStatus.APPROVED));
        nextBooking = bookingRepository.save(new Booking(3, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5), item, booker, BookingStatus.APPROVED));
    }

    @Test
    void findAllByBookerId() {
        List<Booking> res = bookingRepository.findAllByBookerId(booker.getId());

        assertNotNull(res);
        assertEquals(3, res.size());
        assertEquals(booking1.getId(), res.get(0).getId());
    }

    @Test
    void testFindAllByBookerId() {
        Page<Booking> res = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), Pageable.unpaged());

        assertNotNull(res);
        assertEquals(3, res.toList().size());
        assertEquals(booking1.getId(), res.toList().get(1).getId());
        assertEquals(booking1.getBooker(), res.toList().get(0).getBooker());
    }

    @Test
    void findLastBooking() {
        Optional<Booking> res = bookingRepository.findLastBooking(item.getId(), owner.getId());

        assertEquals(lastBooking.getId(), res.get().getId());
        assertEquals(lastBooking.getStart(), res.get().getStart());
    }

    @Test
    void findNextBooking() {
        Optional<Booking> res = bookingRepository.findNextBooking(item.getId(), owner.getId());

        assertEquals(nextBooking.getId(), res.get().getId());
        assertEquals(nextBooking.getEnd(), res.get().getEnd());
    }
}

