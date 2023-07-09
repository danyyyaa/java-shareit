package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto saveUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос на сохранение пользователя: {}", userDto);
        User user = userService.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Получен запрос на обновление пользователя {}", userId);
        User user = userService.update(UserMapper.mapToUser(userDto), userId);
        return UserMapper.mapToUserDto(user);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable long id) {
        log.info("Получен запрос на получение пользователя {}", id);
        User user = userService.findById(id);
        return UserMapper.mapToUserDto(user);
    }

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService
                .findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        log.info("Получен запрос на удаление пользователя {}", id);
        userService.deleteById(id);
    }
}
