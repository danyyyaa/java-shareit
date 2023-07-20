package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constant.TIME_PATTERN;
import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTests {

    @Mock
    private ItemRequestService mockItemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ItemRequest itemRequest;

    private ItemRequestResponseDto itemRequestResponseDto;

    private ItemRequestRequestDto itemRequestRequestDto;

    private final LocalDateTime created = LocalDateTime.now();


    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        itemRequest = ItemRequest.builder()
                .description("description")
                .created(created)
                .build();

        itemRequestResponseDto = ItemRequestResponseDto.builder()
                .description("description")
                .created(created)
                .build();

        itemRequestRequestDto = new ItemRequestRequestDto("description");
    }

    @Test
    void shouldSaveItemRequest() throws Exception {
        when(mockItemRequestService.saveItemRequest(any(), anyLong()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestRequestDto))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription()), String.class));
    }

    @Test
    void shouldFindOnwItemRequests() throws Exception {
        when(mockItemRequestService.findOwnItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription()), String.class));
    }

    @Test
    void shouldFindAllItemRequests() throws Exception {
        when(mockItemRequestService.findAllItemRequests(anyLong(), any()))
                .thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription()), String.class));
    }

    @Test
    void shouldFindItemRequestById() throws Exception {
        when(mockItemRequestService.findItemRequestsById(anyLong(), anyLong()))
                .thenReturn(itemRequestResponseDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header(USER_ID_HEADER, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription()), String.class));
    }
}
