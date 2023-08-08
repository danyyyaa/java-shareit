package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constant.TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingSavingDto {
    private Long itemId;

    @FutureOrPresent
    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime start;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime end;
}
