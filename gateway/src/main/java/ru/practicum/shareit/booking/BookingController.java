package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aspect.ToLog;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.validation.ValuesAllowedConstraint;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@ToLog
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveBooking(@Valid @RequestBody BookingSavingDto bookingSavingDto,
                                              @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.saveBooking(bookingSavingDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
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
        return bookingClient.findAllBookingsByUserId(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateAvailableStatus(@PathVariable long bookingId,
                                                        @RequestParam(required = false) Boolean approved,
                                                        @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.updateAvailableStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingByUserOwner(@PathVariable long bookingId,
                                                         @RequestHeader(value = USER_ID_HEADER) long userId) {
        return bookingClient.findBookingByUserOwner(bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findOwnerBookings(@RequestHeader(USER_ID_HEADER) long userId,
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
        return bookingClient.findOwnerBookings(userId, state, from, size);
    }
}
