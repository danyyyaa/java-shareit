package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constant.TIME_PATTERN;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentResponseDto {
    private Long id;

    private String text;

    @DateTimeFormat(pattern = TIME_PATTERN)
    private LocalDateTime created;

    private String authorName;
}
