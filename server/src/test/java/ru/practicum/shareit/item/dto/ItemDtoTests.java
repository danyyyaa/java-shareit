package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTests {

    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;

    @Test
    void itemDtoTest() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("name")
                .description("description")
                .available(true)
                .requestId(null)
                .build();

        JsonContent<ItemDto> jsonContent = itemDtoJacksonTester.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId());

        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());

        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());

        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());

        assertThat(jsonContent).extractingJsonPathStringValue("$.requestId")
                .isEqualTo(itemDto.getRequestId());
    }
}
