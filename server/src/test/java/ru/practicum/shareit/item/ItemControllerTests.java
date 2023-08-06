package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
class ItemControllerTests {

    @Mock
    private ItemService mockItemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ItemDto itemDto;

    private ItemAllFieldsDto itemAllFieldsDto;

    private Item item;

    private CommentResponseDto commentResponseDto;

    private CommentRequestDto commentRequestDto;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemAllFieldsDto = ItemAllFieldsDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .text("text")
                .authorName("name")
                .build();

        commentRequestDto = new CommentRequestDto("text");
    }

    @Test
    void shouldSaveItem() throws Exception {
        when(mockItemService.save(any(), anyLong()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(mockItemService.update(any(), anyLong(), anyLong()))
                .thenReturn(item);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void shouldFindItemById() throws Exception {
        when(mockItemService.findById(anyLong(), anyLong()))
                .thenReturn(itemAllFieldsDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header(USER_ID_HEADER, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void shouldFindItemsByUserId() throws Exception {
        when(mockItemService.findItemsByUserId(anyLong(), any()))
                .thenReturn(List.of(itemAllFieldsDto));

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemAllFieldsDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].name", is(itemAllFieldsDto.getName()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemAllFieldsDto.getAvailable()), Boolean.class));
    }

    @Test
    void shouldSearchByText() throws Exception {
        when(mockItemService.searchByText(anyString(), anyLong(), any()))
                .thenReturn(List.of(itemAllFieldsDto));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .header(USER_ID_HEADER, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemAllFieldsDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].name", is(itemAllFieldsDto.getName()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemAllFieldsDto.getAvailable()), Boolean.class));
    }

    @Test
    void shouldSaveComment() throws Exception {
        long itemId = 1;
        long userId = 1;

        when(mockItemService.saveComment(eq(itemId), eq(userId), anyString()))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentRequestDto.getText()));
    }
}
