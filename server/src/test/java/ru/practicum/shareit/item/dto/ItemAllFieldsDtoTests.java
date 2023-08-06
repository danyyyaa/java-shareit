package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemAllFieldsDtoTests {

    @Autowired
    private JacksonTester<ItemAllFieldsDto> itemAllFieldsDtoJacksonTester;

    @Test
    void itemAllFieldsDtoTest() throws IOException {
        ItemAllFieldsDto itemAllFieldsDto = ItemAllFieldsDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .itemRequest(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        JsonContent<ItemAllFieldsDto> jsonContent = itemAllFieldsDtoJacksonTester.write(itemAllFieldsDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemAllFieldsDto.getName());

        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemAllFieldsDto.getDescription());

        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemAllFieldsDto.getAvailable());

        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemRequest")
                .isEqualTo(1);

        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking")
                .isEqualTo(itemAllFieldsDto.getLastBooking());

        assertThat(jsonContent).extractingJsonPathStringValue("$.nextBooking")
                .isEqualTo(itemAllFieldsDto.getNextBooking());

        assertThat(jsonContent).extractingJsonPathStringValue("$.comments")
                .isEqualTo(itemAllFieldsDto.getComments());
    }
}
