package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerId(Integer bookerId);

    @Query(" select b from Item i, Booking b " +
            "where b.item.ownerId = ?1 " +
            "order by b.start ")
    List<Booking> findAllByOwnerId(Integer ownerId);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.item.ownerId = ?2 " +
            "and b.status = 'APPROVED'" +
            "and b.end < current_timestamp " +
            "order by b.end ")
    Optional<Booking> findLastBooking(Integer itemId, Integer ownerId);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.item.ownerId = ?2 " +
            "and b.status = 'APPROVED'" +
            "and b.start > current_timestamp " +
            "order by b.start ")
    Optional<Booking> findNextBooking(Integer itemId, Integer ownerId);
}
