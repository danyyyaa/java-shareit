package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item createItem(Item item, long userId);

    Item updateItem(Item item, long itemId, long userId);

    Item getItemById(long userId, long itemId);

    Collection<Item> searchByText(String text, long userId);

    Collection<Item> getItemsByUserId(long userId);
}
