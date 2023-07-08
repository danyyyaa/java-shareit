package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@EqualsAndHashCode(callSuper = true)
@Data
public class FindItemByIdDto extends ItemDto {

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    public FindItemByIdDto(long id, String name, String description, Boolean available,
                           BookingDto lastBooking, BookingDto nextBooking) {
        super(id, name, description, available);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
