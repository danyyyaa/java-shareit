package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (isDuplicateEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(String.format("Email %s уже существует.", user.getEmail()));
        }

        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user, Long userId) {
        User updatedUser = userRepository.getUserById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь %s не найден.", user)));

        Set<String> emails = userRepository.getUsers().stream().map(User::getEmail).collect(Collectors.toSet());

        if (emails.contains(user.getEmail()) && (!user.getEmail().equals(updatedUser.getEmail()))) {
            throw new EmailAlreadyExistsException(String.format("Email %s уже существует.", user.getEmail()));
        }
        if (user.getName() != null && !updatedUser.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updatedUser.getEmail().isBlank()) {
            updatedUser.setEmail(user.getEmail());
        }

        return updatedUser;
    }

    @Override
    public User getUserById(long id) {
        return userRepository.getUserById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь %s не найден.", id)));
    }

    @Override
    public Collection<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }

    private boolean isDuplicateEmail(String userEmail) {
        Set<String> emails = userRepository.getUsers().stream().map(User::getEmail).collect(Collectors.toSet());
        return emails.contains(userEmail);
    }
}
