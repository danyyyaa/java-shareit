package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {

    private long itemId = 1L;

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> createItem(Item item) {
        item.setId(itemId++);
        items.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> updateItem(Item item, long itemId, long userId) {
        return Optional.ofNullable(items.put(itemId, item));
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getItems() {
        return items.values();
    }
}
