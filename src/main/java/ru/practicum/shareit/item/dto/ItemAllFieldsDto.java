package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ItemAllFieldsDto extends ItemDto {

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentResponseDto> comments;

    public ItemAllFieldsDto(long id, String name, String description, Boolean available,
                            BookingDto lastBooking, BookingDto nextBooking, List<CommentResponseDto> comments) {
        super(id, name, description, available);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
