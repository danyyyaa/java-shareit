package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {

    private long itemId = 1L;

    private final Map<Long, Item> items = new HashMap<>();

    private final Map<Long, List<Item>> userItemIndex = new HashMap<>();

    @Override
    public Item createItem(Item item, long userId) {
        item.setId(itemId++);

        items.put(item.getId(), item);

        List<Item> itemList = userItemIndex.computeIfAbsent(userId, k -> new ArrayList<>());
        itemList.add(item);

        return item;
    }

    @Override
    public Item updateItem(Item item, long itemId, long userId) {
        return items.put(itemId, item);
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getItems() {
        return List.copyOf(items.values());
    }

    @Override
    public Collection<Item> getItemsByUserId(long userId) {
        return userItemIndex.get(userId);
    }

    @Override
    public Collection<Item> searchByText(String text, long userId) {
        String lowerCaseText = text.toLowerCase();

        return items
                .values()
                .stream()
                .filter(i -> i.getAvailable() &&
                        (i.getDescription().toLowerCase().contains(lowerCaseText) ||
                                i.getName().toLowerCase().contains(lowerCaseText))
                )
                .collect(Collectors.toList());
    }
}
