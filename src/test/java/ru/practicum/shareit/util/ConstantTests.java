package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstantTests {

    @Test
    void shouldReturnZeroForEqualStartDatesAsc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        assertEquals(0, Constant.orderByStartDateAsc.compare(booking1, booking2));
    }

    @Test
    void shouldReturnNegativeForEarlierStartDateAsc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 22, 15, 30)).build();

        assertTrue(Constant.orderByStartDateAsc.compare(booking1, booking2) < 0);
    }

    @Test
    void shouldReturnPositiveForLaterStartDateAsc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 22, 15, 30)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        assertTrue(Constant.orderByStartDateAsc.compare(booking1, booking2) > 0);
    }

    @Test
    void shouldReturnZeroForEqualStartDatesDesc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        assertEquals(0, Constant.orderByStartDateDesc.compare(booking1, booking2));
    }

    @Test
    void shouldReturnPositiveForEarlierStartDateDesc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 22, 15, 30)).build();

        assertTrue(Constant.orderByStartDateDesc.compare(booking1, booking2) > 0);
    }

    @Test
    void shouldReturnNegativeForLaterStartDateDesc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 22, 15, 30)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        assertTrue(Constant.orderByStartDateDesc.compare(booking1, booking2) < 0);
    }

    @Test
    void shouldReturnZeroForEqualStartAndEndDatesAsc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        assertEquals(0, Constant.orderByStartDateAsc.compare(booking1, booking2));
    }

    @Test
    void shouldReturnZeroForEqualStartAndEndDatesDesc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        assertEquals(0, Constant.orderByStartDateDesc.compare(booking1, booking2));
    }

    @Test
    void shouldReturnSameResultForEqualBookingsAsc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        int result1 = Constant.orderByStartDateAsc.compare(booking1, booking2);
        int result2 = Constant.orderByStartDateAsc.compare(booking2, booking1);

        assertEquals(result1, result2);
    }

    @Test
    void shouldReturnSameResultForEqualBookingsDesc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        int result1 = Constant.orderByStartDateDesc.compare(booking1, booking2);
        int result2 = Constant.orderByStartDateDesc.compare(booking2, booking1);

        assertEquals(result1, result2);
    }

    @Test
    void shouldSortListOfBookingsAsc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 22, 15, 30)).build();

        Booking booking3 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 20, 8, 45)).build();


        List<Booking> bookings = Arrays.asList(booking1, booking2, booking3);

        bookings.sort(Constant.orderByStartDateAsc);

        List<Booking> expectedOrder = List.of(booking3, booking1, booking2);
        assertEquals(expectedOrder, bookings);
    }

    @Test
    void shouldSortListOfBookingsDesc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 21, 10, 0)).build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 22, 15, 30)).build();

        Booking booking3 = Booking.builder()
                .start(LocalDateTime.of(2023, 7, 20, 8, 45)).build();


        List<Booking> bookings = Arrays.asList(booking1, booking2, booking3);

        bookings.sort(Constant.orderByStartDateDesc);

        List<Booking> expectedOrder = List.of(booking2, booking1, booking3);
        assertEquals(expectedOrder, bookings);
    }
}
