package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
        @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', :text, '%')) " +
            "or upper(i.description) like upper(concat('%', :text, '%')) " +
            "and i.available = true ")
    Page<Item> search(String text, Pageable page);

    @Query("select i from Item i " +
            "where i.ownerId = :owner " +
            "order by i.id ")
    Page<Item> findAllByOwner(Integer owner, Pageable page);

    @Query(" select i from Item i " +
            "where i.itemRequest.id = ?1 " +
            "order by i.id desc")
    List<Item> findItemByItemRequestId(Integer itemRequestId);
}
