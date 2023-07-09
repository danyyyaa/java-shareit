package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long ownerId);

    @Query(value = "SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<Item> findItemsByText(String text);
}


