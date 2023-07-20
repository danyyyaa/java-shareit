package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingSavingDtoTest {

    @Autowired
    private JacksonTester<BookingSavingDto> bookingSavingDtoJacksonTester;

    @Test
    void bookingSavingDtoTest() throws IOException {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 12, 0);

        BookingSavingDto bookingSavingDto = BookingSavingDto.builder()
                .itemId(null)
                .start(start)
                .end(end)
                .build();
        JsonContent<BookingSavingDto> jsonContent = bookingSavingDtoJacksonTester.write(bookingSavingDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.itemId")
                .isEqualTo(bookingSavingDto.getItemId());

        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-01-01T12:00:00");

        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-01-02T12:00:00");

    }
}
