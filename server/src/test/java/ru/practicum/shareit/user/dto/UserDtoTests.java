package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class UserDtoTests {

    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Test
    void userDtoTest() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(null)
                .name("name")
                .email("email@email.ru")
                .build();

        JsonContent<UserDto> jsonContent = userDtoJacksonTester.write(userDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.id")
                .isEqualTo(userDto.getId());

        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());

        assertThat(jsonContent).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }
}
