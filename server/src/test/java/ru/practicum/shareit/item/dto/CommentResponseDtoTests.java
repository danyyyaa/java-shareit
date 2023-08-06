package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentResponseDtoTests {

    @Autowired
    private JacksonTester<CommentResponseDto> commentResponseDtoJacksonTester;

    @Test
    void commentResponseDtoTest() throws IOException {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 12, 0);

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(null)
                .text("text")
                .created(time)
                .authorName("name")
                .build();

        JsonContent<CommentResponseDto> jsonContent = commentResponseDtoJacksonTester.write(commentResponseDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.id")
                .isEqualTo(commentResponseDto.getId());

        assertThat(jsonContent).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentResponseDto.getText());

        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-01-01T12:00:00");

        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentResponseDto.getAuthorName());
    }
}
