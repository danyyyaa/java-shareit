package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemGetOwnItemRequestDtoTests {

    @Autowired
    private JacksonTester<ItemGetOwnItemRequestDto> itemGetOwnItemRequestDtoJacksonTester;

    @Test
    void itemGetOwnItemRequestDtoTest() throws IOException {
        ItemGetOwnItemRequestDto itemGetOwnItemRequestDto = ItemGetOwnItemRequestDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemGetOwnItemRequestDto> jsonContent =
                itemGetOwnItemRequestDtoJacksonTester.write(itemGetOwnItemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemGetOwnItemRequestDto.getName());

        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemGetOwnItemRequestDto.getDescription());

        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemGetOwnItemRequestDto.getAvailable());

        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(1);
    }
}
