package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constant.TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAllFieldsDto {

    private long id;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime start;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime end;

    private Item item;

    private User booker;

    private Status status;
}
