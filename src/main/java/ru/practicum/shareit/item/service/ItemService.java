package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.FindItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Item save(Item item, long userId);

    Item update(Item item, long itemId, long userId);

    ItemDto findById(long userId, long itemId);

    Collection<Item> searchByText(String text, long userId);

    Collection<FindItemByIdDto> findItemsByUserId(long userId);
}
