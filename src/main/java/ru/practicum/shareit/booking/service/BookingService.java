package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;

public interface BookingService {

    Booking save(long itemId, LocalDateTime start, LocalDateTime end, long userId);

    Booking findById(long id);

    Booking update(Booking booking, long userId, long itemId);
}
