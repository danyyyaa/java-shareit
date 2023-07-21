package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestResponseDtoTests {

    @Autowired
    private JacksonTester<ItemRequestResponseDto> itemRequestRequestDtoJacksonTester;

    @Test
    void itemRequestResponseDtoTest() throws IOException {
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("description")
                .created(null)
                .items(null)
                .build();

        JsonContent<ItemRequestResponseDto> jsonContent =
                itemRequestRequestDtoJacksonTester.write(itemRequestResponseDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestResponseDto.getDescription());

        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestResponseDto.getCreated());

        assertThat(jsonContent).extractingJsonPathNumberValue("$.items")
                .isEqualTo(itemRequestResponseDto.getItems());
    }
}
