package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Comparator.orderByStartDateAsc;
import static ru.practicum.shareit.util.Comparator.orderByStartDateDesc;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item save(ItemDto itemDto, long ownerId) {
        Item item = ItemMapper.INSTANCE.mapToItem(itemDto);

        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", ownerId)));

        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() ->
                            new NotFoundException(String.format("Запрос %s не найден", itemDto.getRequestId())));
            item.setItemRequest(itemRequest);
        }

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

        return findItemsDto(Collections.singletonList(item), userId).get(0);
    }

    private List<ItemAllFieldsDto> findItemsDto(List<Item> items, long userId) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Booking> bookings;

        if (items.size() == 1) {
            bookings = bookingRepository.findBookingsByItemId(items, userId, Status.APPROVED);
        } else {
            bookings = bookingRepository.findBookingsByItemIn(items);
        }

        List<Comment> comments = commentRepository.findCommentsByItemInOrderByCreated(items);

        Map<Item, List<Booking>> bookingsMap = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getItem));

        Map<Item, List<Comment>> commentsMap = comments.stream()
                .collect(Collectors.groupingBy(Comment::getItem));

        return items.stream()
                .map(item -> {
                    List<CommentResponseDto> itemComments = commentsMap
                            .getOrDefault(item, Collections.emptyList())
                            .stream()
                            .map(CommentMapper.INSTANCE::mapToCommentResponseDto)
                            .collect(Collectors.toList());

                    List<Booking> itemBookings = bookingsMap.getOrDefault(item, Collections.emptyList());

                    Optional<Booking> lastOptional = getLastItem(itemBookings);
                    Optional<Booking> nextOptional = getNextItem(itemBookings);

                    BookingDto last = lastOptional.map(BookingMapper.INSTANCE::mapFromBookingToBookingDto).orElse(null);
                    BookingDto next = nextOptional.map(BookingMapper.INSTANCE::mapFromBookingToBookingDto).orElse(null);

                    return ItemMapper.INSTANCE.mapToItemAllFieldsDto(item, last, next, itemComments);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemAllFieldsDto> searchByText(String text, long userId, Pageable page) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findItemsByText(text, page);
        return findItemsDto(items, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemAllFieldsDto> findItemsByUserId(long userId, Pageable page) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId, page);
        return findItemsDto(items, userId);
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
        return CommentMapper.INSTANCE.mapToCommentResponseDto(savedComment);
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