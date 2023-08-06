package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class GetBookingItemDtoTests {

    @Autowired
    private JacksonTester<GetBookingItemDto> getBookingItemDtoJacksonTester;

    @Test
    void getBookingItemDtoTest() throws IOException {
        GetBookingItemDto getBookingItemDto = new GetBookingItemDto(1L, "name");

        JsonContent<GetBookingItemDto> jsonContent = getBookingItemDtoJacksonTester.write(getBookingItemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(getBookingItemDto.getName());
    }
}
