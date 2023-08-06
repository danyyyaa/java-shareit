package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class GetBookingUserDtoTests {

    @Autowired
    private JacksonTester<GetBookingUserDto> getBookingUserDtoJacksonTester;

    @Test
    void getBookingUserDtoTest() throws IOException {
        GetBookingUserDto getBookingUserDto = new GetBookingUserDto(1L);

        JsonContent<GetBookingUserDto> jsonContent = getBookingUserDtoJacksonTester.write(getBookingUserDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
    }
}
