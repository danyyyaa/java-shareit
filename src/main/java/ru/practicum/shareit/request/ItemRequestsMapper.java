package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemGetOwnItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@UtilityClass
public class ItemRequestsMapper {
    public ItemRequestResponseDto mapToItemRequestResponseDtoWithItemId(ItemRequest itemRequest,
                                                                        List<ItemGetOwnItemRequestDto> dtos) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(dtos)
                .build();
    }
}
