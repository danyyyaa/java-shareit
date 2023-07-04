package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;

import java.util.Collection;

public interface ItemService {

    Item save(Item item, long userId);

    Item update(Item item, long itemId, long userId);

    Item findById(long userId, long itemId);

    Collection<Item> searchByText(String text, long userId);

    Collection<Item> findItemsByUserId(long userId);
}
