package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemGetOwnItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestResponseDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemGetOwnItemRequestDto> items;
}
