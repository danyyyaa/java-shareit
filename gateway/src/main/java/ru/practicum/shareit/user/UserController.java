package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aspect.ToLog;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@ToLog
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public Object saveUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        return userClient.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public Object updateUser(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable long userId) {
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{id}")
    public Object findUserById(@PathVariable long id) {
        return userClient.findUserById(id);
    }

    @GetMapping
    public Object findAllUsers() {
        return userClient.findAllUsers();
    }

    @DeleteMapping("/{id}")
    public Object deleteUserById(@PathVariable long id) {
        try {
            userClient.deleteUserById(id);
            return "User deleted successfully";
        } catch (Exception e) {
            return "Error deleting user";
        }
    }
}
