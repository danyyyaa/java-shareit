package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.enums.Status.*;

@Service
@RequiredArgsConstructor
@Transactional
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
                    "Вещь %s не может быть забронирована ее владельцем", bookerId));
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь должна быть доступна для бронирования");
        }

        if (end.isBefore(LocalDateTime.now()) || start.equals(end)) {
            throw new ValidationException("Время начала бронирования не может совпадать со временем его окончания");
        }

        if (end.isBefore(start)) {
            throw new ValidationException("Время окончания бронирования не может быть раньше времени его начала");
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
    @Transactional(readOnly = true)
    public Collection<Booking> findByUserId(long userId, String stateString, Pageable page) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %s не найден.", userId))
        );

        State state;

        state = State.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker(user, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerCurrent(
                        user, currentMoment, page);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerPast(
                        user, currentMoment, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerFuture(
                        user, currentMoment, page);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(
                        user, Status.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(
                        user, Status.REJECTED, page);
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

        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %s не найден", userId)));

        if (booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException(String.format("Нет доступных бронирований у пользователя %s.", userId));
        }

        if (!booking.getItem().getOwner().getId().equals(userId)
                || !booking.getStatus().equals(WAITING)) {
            throw new ValidationException("Бронирование не может быть обновлено.");
        }

        booking.setStatus(state ? APPROVED : REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking findAllBookingsByUserId(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(
                        String.format("У пользователя %s бронирование %s не найдено.", userId, bookingId)));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", userId)));

        if (!Objects.equals(booking.getBooker().getId(), user.getId())
                && !Objects.equals(booking.getItem().getOwner().getId(), user.getId())) {
            throw new NotFoundException(String.format("Бронирование %s не найдено", booking.getId()));
        }

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> findOwnerBookings(long userId, String stateString, Pageable page) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %s не найден.", userId))
        );

        State state;

        state = State.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwner(
                        user, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerCurrent(
                        user, currentMoment, page);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerPast(
                        user, currentMoment, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerFuture(
                        user, currentMoment, page);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        user, Status.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        user, Status.REJECTED, page);
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings;
    }
}
