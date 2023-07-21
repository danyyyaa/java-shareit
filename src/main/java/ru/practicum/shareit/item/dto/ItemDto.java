package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.marker.Create;
import ru.practicum.shareit.validation.marker.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;

    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    private Long requestId;
}
