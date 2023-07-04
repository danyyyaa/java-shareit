package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
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

        return BookingMapper.mapToBookingAllFieldsDto(booking);
    }

    @GetMapping
    public BookingAllFieldsDto findBookingById(@RequestHeader(USER_ID_HEADER) long userId) {
        Booking booking = bookingService.findById(userId);
        return BookingMapper.mapToBookingAllFieldsDto(booking);
    }

    @PatchMapping
    public BookingAllFieldsDto updateBooking(@RequestBody BookingAllFieldsDto bookingAllFieldsDto,
                                             @RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId) {
        Booking booking = bookingService.update(BookingMapper.mapToBooking(bookingAllFieldsDto), userId, itemId);
        return BookingMapper.mapToBookingAllFieldsDto(booking);
    }
}
