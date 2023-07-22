package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.Status;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingAllFieldsDtoTests {

    @Autowired
    private JacksonTester<BookingAllFieldsDto> itemRequestDtoJacksonTester;

    @Test
    void bookingAllFieldsDtoTest() throws IOException {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 12, 0);

        BookingAllFieldsDto bookingAllFieldsDto = BookingAllFieldsDto.builder()
                .start(start)
                .end(end)
                .item(null)
                .booker(null)
                .status(Status.WAITING)
                .build();

        JsonContent<BookingAllFieldsDto> jsonContent = itemRequestDtoJacksonTester.write(bookingAllFieldsDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-01-01T12:00:00");

        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-01-02T12:00:00");

        assertThat(jsonContent).extractingJsonPathStringValue("$.item")
                .isEqualTo(bookingAllFieldsDto.getItem());

        assertThat(jsonContent).extractingJsonPathStringValue("$.booker")
                .isEqualTo(bookingAllFieldsDto.getBooker());

        assertThat(jsonContent).extractingJsonPathStringValue("$.status")
                .isEqualTo(String.valueOf(bookingAllFieldsDto.getStatus()));
    }
}
