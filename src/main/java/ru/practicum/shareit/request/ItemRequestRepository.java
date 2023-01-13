package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query("select r from ItemRequest r " +
            "where r.requester.id = ?1 " +
            "order by r.created desc")
    List<ItemRequest> findAllByRequesterId(Integer requesterId);

    List<ItemRequest> findAllByRequesterIdNot(Integer requesterId, Pageable page);
}
