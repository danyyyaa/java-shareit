package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemById(long userId, long itemId);

    Collection<ItemDto> searchByText(String text, long userId);

    Collection<ItemDto> getItemsByUserId(long userId);
}
