package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        User user = userService.createUser(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable long userId) {
        User user = userService.updateUser(UserMapper.mapToUser(userDto), userId);
        return UserMapper.mapToUserDto(user);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);
        return UserMapper.mapToUserDto(user);
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService
                .getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);
    }
}
