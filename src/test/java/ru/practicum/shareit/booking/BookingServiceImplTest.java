package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.enums.Status.APPROVED;
import static ru.practicum.shareit.booking.enums.Status.REJECTED;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository mockBookingRepository;

    @Mock
    private ItemRepository mockItemRepository;

    @Mock
    private UserRepository mockUserRepository;

    private Item item;
    private User user;
    private Booking booking;
    private static LocalDateTime start;
    private static LocalDateTime end;
    private static Pageable page;

    @BeforeAll
    static void beforeAll() {
        page = null;
        start = LocalDateTime.now();
        end = LocalDateTime.now();
    }

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(APPROVED)
                .build();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundBookingSave() {
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.save(1L, start, end, 1L));
        verify(mockItemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionIfOwnerIdEqualsBookerIdInSave() {
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        long itemId = item.getId();
        long userId = user.getId();
        assertThrows(NotFoundException.class, () -> bookingService.save(itemId, start, end, userId));
        verify(mockItemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenItemNotAvailableInSave() {
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));


        item.setAvailable(false);
        long itemId = item.getId();
        long userId = 99L;
        assertThrows(ValidationException.class, () -> bookingService.save(itemId, start, end, userId));
        verify(mockItemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenBookingTimeDontValidInSave() {
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        long itemId = item.getId();
        long userId = 99L;
        LocalDateTime min = LocalDateTime.MIN;
        LocalDateTime max = LocalDateTime.MAX;
        LocalDateTime now = LocalDateTime.now();

        assertThrows(ValidationException.class, () -> bookingService.save(itemId, start, min, userId));
        assertThrows(ValidationException.class, () -> bookingService.save(itemId, start, start, userId));
        assertThrows(ValidationException.class, () ->
                bookingService.save(itemId, max, now, userId));
        assertThrows(ValidationException.class, () ->
                bookingService.save(itemId, max, min, userId));
        verify(mockItemRepository, times(4)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenBookerNotFoundInSave() {
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        long itemId = item.getId();
        long userId = 99L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime max = LocalDateTime.MAX;

        assertThrows(NotFoundException.class, () ->
                bookingService.save(itemId, now, max, userId));
        verify(mockItemRepository, times(1)).findById(anyLong());
        verify(mockUserRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldSaveBooking() {
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(mockBookingRepository.save(any()))
                .thenReturn(booking);

        long itemId = item.getId();
        long userId = 99L;

        assertThat(booking, equalTo(bookingService.save(itemId, start, LocalDateTime.MAX, userId)));
        verify(mockItemRepository, times(1)).findById(anyLong());
        verify(mockUserRepository, times(1)).findById(anyLong());
        verify(mockBookingRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInFindUserById() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        long userId = 99L;
        assertThrows(NotFoundException.class, () -> bookingService.findByUserId(userId, "ALL", page));
        verify(mockUserRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldUpdateBookingStatusToApproved() {
        long bookingId = 1L;
        long userId = 3L;
        user.setId(2L);

        Booking existingBooking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.of(existingBooking));

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        User owner = User.builder()
                .id(3L)
                .name("name")
                .email("email@email.ru")
                .build();
        existingBooking.getItem().setOwner(owner);
        Booking result = bookingService.updateAvailableStatus(bookingId, true, userId);

        assertThat(result, equalTo(booking));
        assertThat(result.getStatus(), equalTo(Status.APPROVED));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldUpdateBookingStatusToRejected() {
        long bookingId = 1L;
        long userId = 3L;
        user.setId(2L);

        Booking existingBooking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.of(existingBooking));

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        booking.setStatus(REJECTED);
        when(mockBookingRepository.save(existingBooking))
                .thenReturn(booking);

        User owner = User.builder()
                .id(3L)
                .name("name")
                .email("email@email.ru")
                .build();
        existingBooking.getItem().setOwner(owner);
        Booking result = bookingService.updateAvailableStatus(bookingId, false, userId);

        assertThat(result, equalTo(booking));
        assertThat(result.getStatus(), equalTo(Status.REJECTED));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).save(existingBooking);
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFoundInUpdateAvailableStatus() {
        long bookingId = 1L;
        long userId = 99L;

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.updateAvailableStatus(bookingId, true, userId));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, never()).findById(anyLong());
        verify(mockBookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInUpdateAvailableStatus() {
        long bookingId = 1L;
        long userId = 99L;

        Booking existingBooking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.of(existingBooking));

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.updateAvailableStatus(bookingId, true, userId));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenBookingIsNotWaitingInUpdateAvailableStatus() {
        long bookingId = 1L;
        long userId = 99L;

        Booking existingBooking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.of(existingBooking));

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () ->
                bookingService.updateAvailableStatus(bookingId, true, userId));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldFindAllBookingsByUserIdWithAllState() {
        long userId = 1L;
        String stateString = "ALL";
        List<Booking> expectedBookings = Arrays.asList(
                Booking.builder().id(1L).build(),
                Booking.builder().id(2L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByBooker(user, page))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findByUserId(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByBooker(user, page);
    }

    @Test
    void shouldFindAllBookingsByUserIdWithCurrentState() {
        long userId = 1L;
        String stateString = "CURRENT";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByBookerCurrent(any(), any(), any()))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findByUserId(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByBookerCurrent(any(), any(), any());
    }

    @Test
    void shouldFindAllBookingsByUserIdWithPastState() {
        long userId = 1L;
        String stateString = "PAST";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByBookerPast(any(), any(), any()))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findByUserId(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByBookerPast(any(), any(), any());
    }

    @Test
    void shouldFindAllBookingsByUserIdWithFutureState() {
        long userId = 1L;
        String stateString = "FUTURE";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByBookerFuture(any(), any(), any()))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findByUserId(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByBookerFuture(any(), any(), any());
    }

    @Test
    void shouldFindAllBookingsByUserIdWithWaitingState() {
        long userId = 1L;
        String stateString = "WAITING";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByBookerAndStatus(user, Status.WAITING, page))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findByUserId(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByBookerAndStatus(user, Status.WAITING, page);
    }

    @Test
    void shouldFindAllBookingsByUserIdWithRejectedState() {
        long userId = 1L;
        String stateString = "REJECTED";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByBookerAndStatus(user, Status.REJECTED, page))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findByUserId(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByBookerAndStatus(user, Status.REJECTED, page);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInFindAllBookingsByUserId() {
        long userId = 99L;
        String stateString = "ALL";

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.findByUserId(userId, stateString, page));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, never()).findByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldFindAllBookingsByOwnerWithAllState() {
        long userId = 1L;
        String stateString = "ALL";
        List<Booking> expectedBookings = Arrays.asList(
                Booking.builder().id(1L).build(),
                Booking.builder().id(2L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByItemOwner(user, page))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findOwnerBookings(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByItemOwner(user, page);
    }

    @Test
    void shouldFindAllBookingsByOwnerWithCurrentState() {
        long userId = 1L;
        String stateString = "CURRENT";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByItemOwnerCurrent(any(), any(), any()))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findOwnerBookings(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByItemOwnerCurrent(any(), any(), any());
    }

    @Test
    void shouldFindAllBookingsByOwnerWithPastState() {
        long userId = 1L;
        String stateString = "PAST";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByItemOwnerPast(any(), any(), any()))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findOwnerBookings(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByItemOwnerPast(any(), any(), any());
    }

    @Test
    void shouldFindAllBookingsByOwnerWithFutureState() {
        long userId = 1L;
        String stateString = "FUTURE";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByItemOwnerFuture(any(), any(), any()))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findOwnerBookings(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByItemOwnerFuture(any(), any(), any());
    }

    @Test
    void shouldFindAllBookingsByOwnerWithWaitingState() {
        long userId = 1L;
        String stateString = "WAITING";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByItemOwnerAndStatus(user, Status.WAITING, page))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findOwnerBookings(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByItemOwnerAndStatus(user, Status.WAITING, page);
    }

    @Test
    void shouldFindAllBookingsByOwnerWithRejectedState() {
        long userId = 1L;
        String stateString = "REJECTED";
        List<Booking> expectedBookings = Collections.singletonList(
                Booking.builder().id(1L).build()
        );

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findByItemOwnerAndStatus(user, Status.REJECTED, page))
                .thenReturn(expectedBookings);

        Collection<Booking> result = bookingService.findOwnerBookings(userId, stateString, page);

        assertThat(result, equalTo(expectedBookings));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, times(1)).findByItemOwnerAndStatus(user, Status.REJECTED, page);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInFindAllBookingsByOwner() {
        long userId = 99L;
        String stateString = "ALL";

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.findOwnerBookings(userId, stateString, page));

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockBookingRepository, never()).findByItemOwner(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldFindBookingByBookingIdAndUserIdWhenUserIsBooker() {
        long bookingId = 1L;
        long userId = 99L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(User.builder().id(userId).build())
                .item(Item.builder().owner(User.builder().id(123L).build()).build())
                .build();

        User user = User.builder().id(userId).build();

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Booking result = bookingService.findAllBookingsByUserId(bookingId, userId);
        assertThat(result, equalTo(booking));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, times(1)).findById(userId);
    }

    @Test
    void shouldFindBookingByBookingIdAndUserIdWhenUserIsOwner() {
        long bookingId = 1L;
        long userId = 123L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(User.builder().id(99L).build())
                .item(Item.builder().owner(User.builder().id(userId).build()).build())
                .build();

        User user = User.builder().id(userId).build();

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Booking result = bookingService.findAllBookingsByUserId(bookingId, userId);
        assertThat(result, equalTo(booking));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, times(1)).findById(userId);
    }


    @Test
    void shouldThrowNotFoundExceptionWhenBookingNotFound() {
        long bookingId = 1L;
        long userId = 99L;

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingsByUserId(bookingId, userId));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, never()).findById(anyLong());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        long bookingId = 1L;
        long userId = 99L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(User.builder().id(123L).build())
                .item(Item.builder().owner(User.builder().id(123L).build()).build())
                .build();

        when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingsByUserId(bookingId, userId));

        verify(mockBookingRepository, times(1)).findById(bookingId);
        verify(mockUserRepository, times(1)).findById(userId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNoAvailableBookingsForUser() {
        when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(mockUserRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        long bookingId = booking.getId();

        assertThrows(NotFoundException.class, () -> bookingService.updateAvailableStatus(
                bookingId, true, bookingId));
        verify(mockBookingRepository, times(1)).findById(booking.getId());
    }
}

