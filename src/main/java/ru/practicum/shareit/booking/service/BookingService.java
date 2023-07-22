package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingService {

    Booking save(long itemId, LocalDateTime start, LocalDateTime end, long userId);

    Collection<Booking> findByUserId(long userId, String state, Pageable page);

    Booking updateAvailableStatus(long bookingId, Boolean state, long userId);

    Booking findAllBookingsByUserId(long bookingId, long userId);

    Collection<Booking> findOwnerBookings(long userId, String state, Pageable page);
}
