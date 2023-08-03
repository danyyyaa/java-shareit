package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.GetBookingItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.GetBookingUserDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "item", expression = "java(mapToGetBookingItemDto(booking.getItem()))")
    @Mapping(target = "booker", expression = "java(mapToGetBookingUserDto(booking.getBooker()))")
    BookingAllFieldsDto mapToBookingAllFieldsDto(Booking booking);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingDto mapFromBookingToBookingDto(Booking booking);

    default GetBookingItemDto mapToGetBookingItemDto(Item item) {
        return new GetBookingItemDto(item.getId(), item.getName());
    }

    default GetBookingUserDto mapToGetBookingUserDto(User user) {
        return new GetBookingUserDto(user.getId());
    }
}
