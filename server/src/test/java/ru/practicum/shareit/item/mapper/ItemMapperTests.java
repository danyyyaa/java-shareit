package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.ItemRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ItemMapperTests {

    @Test
    void mapToItemDtoTest() {
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .itemRequest(new ItemRequest())
                .build();

        ItemDto itemDto = ItemMapper.INSTANCE.mapToItemDto(item);

        assertThat(item.getId(), equalTo(itemDto.getId()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getItemRequest().getId(), equalTo(itemDto.getRequestId()));
    }
}
