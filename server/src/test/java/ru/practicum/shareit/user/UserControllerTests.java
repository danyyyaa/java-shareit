package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

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

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private UserController userController;

    private MockMvc mvc;

    private UserDto userDto;

    private User user;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        userDto = UserDto.builder()
                .name("name")
                .email("email@email.ru")
                .build();

        user = User.builder()
                .name("name")
                .email("email@email.ru")
                .build();
    }

    @Test
    void shouldSaveUser() throws Exception {
        when(mockUserService.save(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());

        verify(mockUserService, times(1)).save(any());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        when(mockUserService.update(any(), anyLong()))
                .thenReturn(user);

        mvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());

        verify(mockUserService, times(1)).update(any(), anyLong());
    }

    @Test
    void shouldFindUserById() throws Exception {
        when(mockUserService.findById(anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());

        verify(mockUserService, times(1)).findById(anyLong());
    }

    @Test
    void shouldFindAllUsers() throws Exception {
        when(mockUserService.findAll())
                .thenReturn(List.of(user));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteUserById() throws Exception {
        mvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());
    }
}
