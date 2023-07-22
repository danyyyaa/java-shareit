package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.GetBookingItemDto;
import ru.practicum.shareit.user.dto.GetBookingUserDto;

@UtilityClass
public class BookingMapper {
    public BookingAllFieldsDto mapToBookingAllFieldsDto(Booking booking) {
        return BookingAllFieldsDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new GetBookingItemDto(booking.getItem().getId(), booking.getItem().getName()))
                .booker(new GetBookingUserDto(booking.getBooker().getId()))
                .status(booking.getStatus())
                .build();
    }

    public BookingDto mapFromBookingToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
