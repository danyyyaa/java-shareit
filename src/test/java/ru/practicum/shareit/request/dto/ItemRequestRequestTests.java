package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestRequestTests {

    @Autowired
    private JacksonTester<ItemRequestRequestDto> itemRequestRequestDtoJacksonTester;

    @Test
    void itemRequestRequestDtoTest() throws IOException {
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto("description");

        JsonContent<ItemRequestRequestDto> jsonContent =
                itemRequestRequestDtoJacksonTester.write(itemRequestRequestDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestRequestDto.getDescription());
    }
}
