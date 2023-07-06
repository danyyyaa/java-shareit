package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingTimeState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.enums.BookingState.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public Booking save(long itemId, LocalDateTime start, LocalDateTime end, long bookerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь %s не найдена.", itemId)));

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException(String.format(
                    "Вещь с id %s не может быть забронирована ее владельцем", bookerId));
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь должна быть доступна для бронирования");
        }

        if (end.isBefore(LocalDateTime.now()) || start.equals(end)) {
            throw new ValidationException("Ошибка валидации.");
        }

        if (end.isBefore(start)) {
            throw new ValidationException("Ошибка валидации.");
        }

        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", bookerId)));

        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(WAITING)
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    public Collection<Booking> findByUserId(long userId, String stateString) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %s не найден.", userId))
        );

        BookingTimeState state;

        state = BookingTimeState.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker(user, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerCurrent(
                        user, currentMoment, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByBookerPast(
                        user, currentMoment, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerFuture(
                        user, currentMoment, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(
                        user, BookingTimeState.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(
                        user, BookingTimeState.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                bookings = Collections.emptyList();
        }
        return bookings;
    }

    @Override
    public Booking updateAvailableStatus(long bookingId, Boolean state, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование %s не найдено.", bookingId)));

        if (booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException(String.format("Нет доступных бронирований у пользователя %s.", userId));
        }

        if (!booking.getItem().getOwner().getId().equals(userId)
                || !booking.getStatus().equals(WAITING)) {
            throw new ValidationException("Бронирование не может быть обновлено.");
        }

        booking.setStatus(Boolean.TRUE.equals(state) ? APPROVED : REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking findAllBookingsByUserId(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %d не найден.", userId)));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден")
        );

        if (!Objects.equals(booking.getBooker().getId(), user.getId())
                && !Objects.equals(booking.getItem().getOwner().getId(), user.getId())) {
            throw new NotFoundException("Бронирование не найдено");
        }

        return booking;
    }

    @Override
    public Collection<Booking> findOwnerBookings(long userId, String stateString) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );

        BookingTimeState state;

        state = BookingTimeState.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwner(
                        user, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerCurrent(
                        user, currentMoment, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerPast(
                        user, currentMoment, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerFuture(
                        user, currentMoment, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        user, BookingTimeState.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        user, BookingTimeState.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings;
    }
}
