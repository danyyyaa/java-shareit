package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerId(long ownerId);

    @Query(value = "SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    Collection<Item> findItemsByText(String text);
}


