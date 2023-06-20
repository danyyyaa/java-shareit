package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepositoryImpl implements UserRepository {

    private long userId = 1L;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> createUser(User user) {
        user.setId(userId++);
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(User user, long userId) {
        return Optional.ofNullable(users.put(userId, user));
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public void deleteUserById(long id) {
        users.remove(id);
    }
}
