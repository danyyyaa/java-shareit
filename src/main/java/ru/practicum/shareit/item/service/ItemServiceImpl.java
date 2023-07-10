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
import ru.practicum.shareit.item.CommentRepository;
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
import java.util.*;
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
    public Collection<ItemAllFieldsDto> searchByText(String text, long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findItemsByText(text);
        return findItemsDto(items);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemAllFieldsDto> findItemsByUserId(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return findItemsDto(items);
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
            throw new ValidationException(String.format("У пользователя %s нету бронирований", user.getId()));
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

    private Collection<ItemAllFieldsDto> findItemsDto(List<Item> items) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findCommentsByItemIdInOrderByCreated(itemIds);

        Map<Long, List<Comment>> commentsMap = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<Booking> bookings = bookingRepository.findBookingsByItemIdIn(itemIds);

        Map<Long, List<Booking>> bookingsMap = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<CommentResponseDto> itemComments = commentsMap.getOrDefault(
                                    item.getId(), Collections.emptyList())
                            .stream()
                            .map(CommentMapper::mapToCommentResponseDto)
                            .collect(Collectors.toList());

                    List<Booking> itemBookings = bookingsMap.getOrDefault(item.getId(), Collections.emptyList());
                    Optional<Booking> lastOptional = getLastItem(itemBookings);
                    Optional<Booking> nextOptional = getNextItem(itemBookings);

                    BookingDto last = lastOptional.map(BookingMapper::mapFromBookingToBookingDto).orElse(null);
                    BookingDto next = nextOptional.map(BookingMapper::mapFromBookingToBookingDto).orElse(null);

                    return ItemMapper.mapToItemAllFieldsDto(item, last, next, itemComments);
                })
                .collect(Collectors.toList());
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
