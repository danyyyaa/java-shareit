package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {

    User createUser(User user);

    User updateUser(User user, long userId);

    User getUserById(long id);

    Collection<User> getUsers();

    void deleteUserById(long id);
}
