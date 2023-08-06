package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentRequestDtoTests {

    @Autowired
    private JacksonTester<CommentRequestDto> commentRequestDtoJacksonTester;

    @Test
    void commentRequestDtoTest() throws IOException {
        CommentRequestDto commentRequestDto = new CommentRequestDto("text");

        JsonContent<CommentRequestDto> jsonContent = commentRequestDtoJacksonTester.write(commentRequestDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentRequestDto.getText());
    }
}
