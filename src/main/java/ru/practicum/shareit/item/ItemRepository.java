package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId, Pageable page);

    @Query(value = "SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(CONCAT('%', :text, '%')))")
    List<Item> findItemsByText(@Param("text") String text, Pageable page);

    List<Item> findItemByItemRequestIn(List<ItemRequest> requests);
}


