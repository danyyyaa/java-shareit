package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.ValuesAllowedConstraint;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingAllFieldsDto saveBooking(@Valid @RequestBody BookingSavingDto bookingSavingDto,
                                           @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получена сущность: {}", bookingSavingDto);
        Booking booking = bookingService.save(
                bookingSavingDto.getItemId(),
                bookingSavingDto.getStart(),
                bookingSavingDto.getEnd(),
                userId);

        return BookingMapper.mapToBookingAllFieldsDto(booking);
    }

    @GetMapping
    public Collection<BookingAllFieldsDto> findAllBookingsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                                   @ValuesAllowedConstraint(propName = "state",
                                                                           values = {"all",
                                                                                   "current",
                                                                                   "past",
                                                                                   "future",
                                                                                   "waiting",
                                                                                   "rejected"},
                                                                           message = "Unknown state: UNSUPPORTED_STATUS")
                                                                   @RequestParam(defaultValue = "all") String state) {
        log.info("Получен запрос на получение всех бронирований пользователя {}", userId);
        return bookingService.findByUserId(userId, state)
                .stream()
                .map(BookingMapper::mapToBookingAllFieldsDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{bookingId}")
    public BookingAllFieldsDto updateAvailableStatus(@PathVariable long bookingId,
                                                     @RequestParam(required = false) Boolean approved,
                                                     @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос на обновление статуса бронирования {}", bookingId);
        Booking booking = bookingService.updateAvailableStatus(bookingId, approved, userId);
        return BookingMapper.mapToBookingAllFieldsDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingAllFieldsDto findBookingByUserOwner(@PathVariable long bookingId,
                                                      @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("Получен запрос на получение бронирования {}", bookingId);
        Booking booking = bookingService.findAllBookingsByUserId(bookingId, userId);

        return BookingMapper.mapToBookingAllFieldsDto(booking);
    }

    @GetMapping("/owner")
    public Collection<BookingAllFieldsDto> findOwnerBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                                             @ValuesAllowedConstraint(propName = "state",
                                                                     values = {"all",
                                                                             "current",
                                                                             "past",
                                                                             "future",
                                                                             "waiting",
                                                                             "rejected"},
                                                                     message = "Unknown state: UNSUPPORTED_STATUS")
                                                             @RequestParam(defaultValue = "all") String state) {
        log.info("Получен запрос на получение всех бронирований пользователя {}", userId);
        return bookingService.findOwnerBookings(userId, state)
                .stream()
                .map(BookingMapper::mapToBookingAllFieldsDto)
                .collect(Collectors.toList());
    }
}
