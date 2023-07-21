package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class BookingMapperTests {

    @Test
    void mapFromBookingToBookingDtoTest() {
        User user = User.builder()
                .id(1L)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .build();

        BookingDto bookingDto = BookingMapper.mapFromBookingToBookingDto(booking);

        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getBookerId(), equalTo(booking.getBooker().getId()));
    }

}
