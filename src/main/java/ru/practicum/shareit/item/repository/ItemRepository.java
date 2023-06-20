package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> createItem(Item item);

    Optional<Item> updateItem(Item item, long itemId, long userId);

    Optional<Item> getItemById(long itemId);

    Collection<Item> getItems();

    Collection<Item> getItemsByUserId(long userId);

    Collection<Item> searchByText(String text, long userId);
}
