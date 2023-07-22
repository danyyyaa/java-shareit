package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aspect.ToLog;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.ValuesAllowedConstraint;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constant.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@ToLog
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingAllFieldsDto saveBooking(@Valid @RequestBody BookingSavingDto bookingSavingDto,
                                           @RequestHeader(USER_ID_HEADER) long userId) {
        Booking booking = bookingService.save(
                bookingSavingDto.getItemId(),
                bookingSavingDto.getStart(),
                bookingSavingDto.getEnd(),
                userId);

        return BookingMapper.INSTANCE.mapToBookingAllFieldsDto(booking);
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
                                                                   @RequestParam(defaultValue = "all") String state,
                                                                   @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Short from,
                                                                   @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Short size) {
        Pageable page = PageRequest.of(from / size, size, SORT_BY_START_DATE_DESC);
        return bookingService.findByUserId(userId, state, page)
                .stream()
                .map(BookingMapper.INSTANCE::mapToBookingAllFieldsDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{bookingId}")
    public BookingAllFieldsDto updateAvailableStatus(@PathVariable long bookingId,
                                                     @RequestParam(required = false) Boolean approved,
                                                     @RequestHeader(USER_ID_HEADER) long userId) {
        Booking booking = bookingService.updateAvailableStatus(bookingId, approved, userId);
        return BookingMapper.INSTANCE.mapToBookingAllFieldsDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingAllFieldsDto findBookingByUserOwner(@PathVariable long bookingId,
                                                      @RequestHeader(value = USER_ID_HEADER) long userId) {
        Booking booking = bookingService.findAllBookingsByUserId(bookingId, userId);

        return BookingMapper.INSTANCE.mapToBookingAllFieldsDto(booking);
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
                                                             @RequestParam(defaultValue = "all") String state,
                                                             @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Short from,
                                                             @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Short size) {

        Pageable page = PageRequest.of(from / size, size, SORT_BY_START_DATE_DESC);
        return bookingService.findOwnerBookings(userId, state, page)
                .stream()
                .map(BookingMapper.INSTANCE::mapToBookingAllFieldsDto)
                .collect(Collectors.toList());
    }
}
