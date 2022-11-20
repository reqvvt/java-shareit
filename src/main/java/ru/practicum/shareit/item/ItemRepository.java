package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', :text, '%')) " +
            "or upper(i.description) like upper(concat('%', :text, '%')) " +
            "and i.available = true ")
    List<Item> search(String text);

    @Query("select i from Item i " +
            "where i.ownerId = :owner " +
            "order by i.id ")
    List<Item> findAllByOwner(Integer owner);
}
