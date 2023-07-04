package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;

public interface BookingService {

    Booking createBooking(long itemId, LocalDateTime start, LocalDateTime end, long userId);

    Booking getBookingById(long id);

    Booking updateBooking(Booking booking, long userId, long itemId);
}
