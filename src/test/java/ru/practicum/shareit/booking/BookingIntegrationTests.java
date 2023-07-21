package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.enums.Status.APPROVED;
import static ru.practicum.shareit.booking.enums.Status.WAITING;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingIntegrationTests {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final EntityManager entityManager;

    private Item item;
    private User user;
    private ItemDto itemDto;
    private static LocalDateTime start;
    private static LocalDateTime end;
    private static Pageable page;

    @BeforeAll
    static void beforeAll() {
        page = null;
        start = LocalDateTime.MIN;
        end = LocalDateTime.MAX;
    }

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();


        user = User.builder()
                .name("name")
                .email("email@email.ru")
                .build();

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();
    }

    @Test
    @DirtiesContext
    void shouldSaveBooking() {
        userService.save(user);

        User secondUser = User.builder()
                .email("second@email.ru")
                .name("name")
                .build();

        userService.save(secondUser);

        itemService.save(itemDto, 1L);

        bookingService.save(1L, start, end, 2L);

        TypedQuery<Booking> query = entityManager
                .createQuery("select b from Booking b where b.id = :id", Booking.class);

        user.setId(1L);
        item.setId(1L);
        Booking bookingFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(bookingFromDb.getStart(), equalTo(start));
        assertThat(bookingFromDb.getEnd(), equalTo(end));
        assertThat(bookingFromDb.getStatus(), equalTo(WAITING));
    }

    @Test
    @DirtiesContext
    void shouldFindBookingsByUserId() {
        userService.save(user);

        User secondUser = User.builder()
                .email("second@email.ru")
                .name("name")
                .build();

        userService.save(secondUser);
        itemService.save(itemDto, 1L);
        bookingService.save(1L, start, end, 2L);

        Collection<Booking> bookings = bookingService.findByUserId(2L, "all", page);

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    @DirtiesContext
    void shouldUpdateAvailableStatus() {
        userService.save(user);

        User secondUser = User.builder()
                .email("second@email.ru")
                .name("name")
                .build();

        userService.save(secondUser);
        itemService.save(itemDto, 1L);
        bookingService.save(1L, start, end, 2L);

        bookingService.updateAvailableStatus(1L, true, 1L);

        TypedQuery<Booking> query = entityManager
                .createQuery("select b from Booking b where b.id = :id", Booking.class);

        Booking bookingFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(bookingFromDb.getStatus(), equalTo(APPROVED));
    }

    @Test
    @DirtiesContext
    void shouldFindAllBookingsByUserId() {
        userService.save(user);

        User secondUser = User.builder()
                .email("second@email.ru")
                .name("name")
                .build();

        userService.save(secondUser);
        itemService.save(itemDto, 1L);
        bookingService.save(1L, start, end, 2L);

        Booking newBooking = bookingService.findAllBookingsByUserId(1L, 1L);

        secondUser.setId(1L);
        assertThat(newBooking.getBooker(), equalTo(secondUser));
    }

    @Test
    @DirtiesContext
    void shouldFindOwnerBookings() {
        userService.save(user);

        User secondUser = User.builder()
                .email("second@email.ru")
                .name("name")
                .build();

        userService.save(secondUser);
        itemService.save(itemDto, 1L);
        bookingService.save(1L, start, end, 2L);

        Collection<Booking> bookings = bookingService.findOwnerBookings(1L, "waiting", page);

        assertThat(bookings.size(), equalTo(1));
    }

}
