package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {

    User save(User user);

    User update(User user, long userId);

    User findById(long id);

    Collection<User> findAll();

    void deleteById(long id);
}
