package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@Data
@AllArgsConstructor
@Builder
public class ItemAllFieldsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long itemRequest;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentResponseDto> comments;
}
