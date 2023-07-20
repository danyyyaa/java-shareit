package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constant.TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSavingDto {
    private Long itemId;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime end;
}
