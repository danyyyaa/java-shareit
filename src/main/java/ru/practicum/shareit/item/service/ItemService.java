package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Item save(ItemDto item, long userId);

    Item update(Item item, long itemId, long userId);
    
    ItemAllFieldsDto findById(long userId, long itemId);

    Collection<ItemAllFieldsDto> searchByText(String text, long userId, Pageable page);

    Collection<ItemAllFieldsDto> findItemsByUserId(long userId, Pageable page);

    CommentResponseDto saveComment(long itemId, long userId, String text);
}
