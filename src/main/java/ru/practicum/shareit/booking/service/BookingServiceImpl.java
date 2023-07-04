package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public Booking createBooking(long itemId, LocalDateTime start, LocalDateTime end, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", itemId)));

        if (!isValid(item, start, end)) {
            throw new ValidationException("Ошибка валидации.");
        }

        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", userId)));

        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingState.WAITING)
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования %s не существует", id)));
    }

    @Override
    public Booking updateBooking(Booking booking, long userId, long itemId) {
        return null;
    }

    private boolean isValid(Item item, LocalDateTime start, LocalDateTime end) {
        boolean isValid = false;

        if (Boolean.FALSE.equals(item.getAvailable())) {
            return isValid;
        }

        if (end.isBefore(LocalDateTime.now()) || start.equals(end)) {
            return isValid;
        }

        if (end.isBefore(start)) {
            return isValid;
        }

        return !isValid;
    }
}
