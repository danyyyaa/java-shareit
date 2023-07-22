package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetOwnItemRequestDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "itemRequest.id", target = "requestId")
    ItemDto mapToItemDto(Item item);

    @Mapping(source = "itemRequest.id", target = "requestId")
    ItemGetOwnItemRequestDto mapFromItemToItemGetOwnItemRequestDto(Item item);

    default long mapItemRequestToLong(ItemRequest itemRequest) {
        return itemRequest != null ? itemRequest.getId() : 0L;
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "getIdToString")
    Item mapToItem(ItemDto itemDto);

    @Named("getIdToString")
    default Long getIdToString(Long id) {
        return id;
    }

    @Mapping(source = "id", target = "id")
    BookingDto mapLongToBookingDtoId(Long id);

    @Mapping(source = "item.id", target = "id") // Explicitly specify the source property for 'id' mapping
    ItemAllFieldsDto mapToItemAllFieldsDto(Item item, BookingDto lastBooking,
                                           BookingDto nextBooking, Collection<CommentResponseDto> comments);
}
