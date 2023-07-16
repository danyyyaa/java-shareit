package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto mapToItemDto(Item item) {
        if (item.getItemRequest() == null) {
            return ItemDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .build();
        } else {
            return ItemDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .requestId(item.getItemRequest().getId())
                    .build();
        }
    }

    public ItemAllFieldsDto mapToItemAllFieldsDto(Item item, BookingDto lastBooking,
                                                  BookingDto nextBooking, List<CommentResponseDto> comments) {
        return ItemAllFieldsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public ItemGetOwnItemRequestDto mapFromItemToItemGetOwnItemRequestDto(Item item) {
        return ItemGetOwnItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(item.getItemRequest().getId())
                .available(item.getAvailable())
                .build();
    }
}
