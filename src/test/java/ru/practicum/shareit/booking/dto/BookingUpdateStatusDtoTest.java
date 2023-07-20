package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.enums.Status.APPROVED;

@JsonTest
class BookingUpdateStatusDtoTest {

    @Autowired
    private JacksonTester<BookingUpdateStatusDto> bookingUpdateStatusDtoJacksonTester;

    @Test
    void bookingUpdateStatusDto() throws IOException {
        BookingUpdateStatusDto bookingUpdateStatusDto = new BookingUpdateStatusDto(APPROVED);

        JsonContent<BookingUpdateStatusDto> jsonContent =
                bookingUpdateStatusDtoJacksonTester.write(bookingUpdateStatusDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.status")
                .isEqualTo(String.valueOf(APPROVED));
    }
}
