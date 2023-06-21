package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepositoryImpl implements UserRepository {

    private long userId = 1L;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(userId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user, long userId) {
        return users.put(userId, user);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public void deleteUserById(long id) {
        users.remove(id);
    }
}
