package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Item save(Item item, long userId);

    Item update(Item item, long itemId, long userId);

    ItemDto findById(long userId, long itemId);

    Collection<ItemAllFieldsDto> searchByText(String text, long userId);

    Collection<ItemAllFieldsDto> findItemsByUserId(long userId);

    CommentResponseDto saveComment(long itemId, long userId, String text);
}
