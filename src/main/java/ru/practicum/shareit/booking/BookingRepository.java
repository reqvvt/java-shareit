package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerId(Integer bookerId);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.item.ownerId = :ownerId " +
            "and b.status = 'APPROVED'" +
            "and b.end < current_timestamp " +
            "order by b.end ")
    Optional<Booking> findLastBooking(Integer itemId, Integer ownerId);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.item.ownerId = :ownerId " +
            "and b.status = 'APPROVED'" +
            "and b.start > current_timestamp " +
            "order by b.start ")
    Optional<Booking> findNextBooking(Integer itemId, Integer ownerId);

    List<Booking> findByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(Integer bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
            Integer bookerId, LocalDateTime end, BookingStatus status);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(
            Integer bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Integer bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdInOrderByStartDesc(List<Integer> itemIds);

    List<Booking> findByItemIdInAndStatusEqualsOrderByStartDesc(List<Integer> itemIds, BookingStatus status);

    List<Booking> findByItemIdInAndEndIsBeforeAndStatusEqualsOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime end, BookingStatus status);

    List<Booking> findByItemIdInAndStartIsAfterOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime start);

    List<Booking> findByItemIdInAndStartBeforeAndEndIsAfterOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime start, LocalDateTime end);
}
