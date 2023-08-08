package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemGetOwnItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemGetOwnItemRequestDto> items;
}
