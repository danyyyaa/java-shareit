package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    User updateUser(User user, long userId);

    Optional<User> getUserById(long id);

    Collection<User> getUsers();

    void deleteUserById(long id);
}
