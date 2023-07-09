package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constant.orderByStartDateAsc;
import static ru.practicum.shareit.util.Constant.orderByStartDateDesc;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

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
    public ItemAllFieldsDto findById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Item %s не найден.", itemId)));

        List<Booking> bookings = bookingRepository.findBookingsByItemId(itemId, userId, Status.APPROVED);

        Optional<Booking> lastOptional = getLastItem(bookings);
        Optional<Booking> nextOptional = getNextItem(bookings);

        List<CommentResponseDto> comments = commentRepository.findCommentByItem_IdIsOrderByCreated(itemId)
                .stream()
                .map(CommentMapper::mapToCommentResponseDto)
                .collect(Collectors.toList());

        if (lastOptional.isEmpty() && nextOptional.isEmpty()) {
            return ItemMapper.mapToItemAllFieldsDto(item, null, null, comments);
        } else if (lastOptional.isPresent() && nextOptional.isEmpty()) {
            BookingDto last = BookingMapper.mapFromBookingToBookingDto(lastOptional.get());
            return ItemMapper.mapToItemAllFieldsDto(item, last, null, comments);
        } else if (lastOptional.isEmpty()) {
            BookingDto next = BookingMapper.mapFromBookingToBookingDto(nextOptional.get());
            return ItemMapper.mapToItemAllFieldsDto(item, null, next, comments);
        }

        BookingDto last = BookingMapper.mapFromBookingToBookingDto(lastOptional.get());
        BookingDto next = BookingMapper.mapFromBookingToBookingDto(nextOptional.get());

        return ItemMapper.mapToItemAllFieldsDto(item, last, next, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemAllFieldsDto> findItemsByUserId(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return items
                .stream()
                .map(item -> findById(userId, item.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto saveComment(long itemId, long userId, String text) {
        if (text.isBlank()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", userId)));


        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Item %s не найден.", itemId)));

        if (bookingRepository.findByBooker(user, Sort.unsorted()).isEmpty()) {
            throw new ValidationException("Пользователь не может оставить комментарий.");
        }

        List<Booking> bookings = bookingRepository.findBookingByItemIdAndStatusNotInAndStartBefore(itemId,
                List.of(Status.REJECTED), LocalDateTime.now());
        if (bookings == null || bookings.isEmpty()) {
            throw new ValidationException("Требуется бронирование для создания комментария ");
        }

        Comment comment = Comment.builder()
                .text(text)
                .created(LocalDateTime.now())
                .item(item)
                .author(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.mapToCommentResponseDto(savedComment);
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
        LocalDateTime currentTime = LocalDateTime.now();
        return bookings.stream()
                .sorted(orderByStartDateAsc)
                .filter(t -> t.getStart().isAfter(currentTime) &&
                        t.getStatus().equals(Status.APPROVED))
                .findFirst();
    }

    private Optional<Booking> getLastItem(List<Booking> bookings) {
        LocalDateTime currentTime = LocalDateTime.now();
        return bookings.stream()
                .sorted(orderByStartDateDesc)
                .filter(t -> t.getStart().isBefore(currentTime) &&
                        t.getStatus().equals(Status.APPROVED))
                .findFirst();
    }
}
