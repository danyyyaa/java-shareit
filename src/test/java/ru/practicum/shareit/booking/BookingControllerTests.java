package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
class BookingControllerTests {

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingService mockBookingService;

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Booking booking;

    private BookingSavingDto bookingSavingDto;

    @BeforeEach
    void init() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.MAX;

        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(Status.APPROVED)
                .start(start)
                .end(end)
                .build();

        bookingSavingDto = BookingSavingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();
    }

    @Test
    void shouldSaveBooking() throws Exception {
        when(mockBookingService.save(anyLong(), any(), any(), anyLong()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingSavingDto))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start[0]", is(bookingSavingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingSavingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingSavingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.end[0]", is(bookingSavingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingSavingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingSavingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void shouldFindAllBookingsByUserId() throws Exception {
        when(mockBookingService.findByUserId(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start[0]", is(bookingSavingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingSavingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingSavingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingSavingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingSavingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingSavingDto.getEnd().getDayOfMonth())));
    }

    @Test
    void shouldUpdateAvailableStatus() throws Exception {
        when(mockBookingService.updateAvailableStatus(anyLong(), any(), anyLong()))
                .thenReturn(booking);


        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start[0]", is(bookingSavingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingSavingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingSavingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.end[0]", is(bookingSavingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingSavingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingSavingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void shouldFindBookingByUserOwner() throws Exception {
        when(mockBookingService.findAllBookingsByUserId(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(USER_ID_HEADER, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start[0]", is(bookingSavingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingSavingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingSavingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.end[0]", is(bookingSavingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingSavingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingSavingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void shouldFindOwnerBookings() throws Exception {
        when(mockBookingService.findOwnerBookings(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1)
                )
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].start[0]", is(bookingSavingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingSavingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingSavingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingSavingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingSavingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingSavingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(booking.getStatus()))));
    }
}
