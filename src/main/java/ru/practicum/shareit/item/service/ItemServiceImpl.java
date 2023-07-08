package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.FindItemByIdDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    @Override
    public Item save(Item item, long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", ownerId)));

        item.setOwner(owner);

        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item, long itemId, long userId) {
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Предмет не найден: %s", item)));

        if (updatedItem.getOwner().getId() != userId) {
            throw new NotFoundException(
                    String.format("У пользователя %d нету предмета %s.", userId, item));
        }

        if (item.getName() != null && !item.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        itemRepository.save(updatedItem);

        return updatedItem;
    }

    @Override
    @Transactional(readOnly = true)
    public FindItemByIdDto findById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Item %s не найден.", itemId)));

        List<Booking> bookings = bookingRepository.findBookingsByItemId(itemId, userId);

        Optional<Booking> lastOptional = getLastItem(bookings);
        Optional<Booking> nextOptional = getNextItem(bookings);

        if (lastOptional.isEmpty() && nextOptional.isEmpty()) {
            return ItemMapper.mapToFindByItemDto(item, null, null);
        }

        assert (lastOptional.isPresent());
        assert (nextOptional.isPresent());
        BookingDto last = BookingMapper.mapFromBookingToBookingDto(lastOptional.get());
        BookingDto next = BookingMapper.mapFromBookingToBookingDto(nextOptional.get());

        return ItemMapper.mapToFindByItemDto(item, last, next);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<FindItemByIdDto> findItemsByUserId(long userId) {
        List<Item> items = (List<Item>) itemRepository.findAllByOwnerId(userId);

        return items
                .stream()
                .map(item -> findById(userId, item.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Item> searchByText(String text, long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findItemsByText(text);
    }

    private Optional<Booking> getNextItem(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now()))
                .min(comparing(Booking::getEnd));

    }

    private Optional<Booking> getLastItem(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(now()))
                .max(comparing(Booking::getEnd));
    }
}
