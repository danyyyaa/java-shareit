package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemGetOwnItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(target = "id", source = "itemRequest.id")
    @Mapping(target = "description", source = "itemRequest.description")
    @Mapping(target = "created", source = "itemRequest.created")
    @Mapping(target = "items", source = "dtos")
    ItemRequestResponseDto mapToItemRequestResponseDtoWithItemId(ItemRequest itemRequest,
                                                                 Collection<ItemGetOwnItemRequestDto> dtos);
}

