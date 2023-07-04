package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;

@UtilityClass
public class BookingMapper {

    public Booking mapToBooking(BookingAllFieldsDto bookingAllFieldsDto) {
        return Booking.builder()
                .id(bookingAllFieldsDto.getId())
                .start(bookingAllFieldsDto.getStart())
                .end(bookingAllFieldsDto.getEnd())
                .item(bookingAllFieldsDto.getItem())
                .booker(bookingAllFieldsDto.getBooker())
                .status(bookingAllFieldsDto.getStatus())
                .build();
    }

    public BookingAllFieldsDto mapToBookingAllFieldsDto(Booking booking) {
        return BookingAllFieldsDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }
}
