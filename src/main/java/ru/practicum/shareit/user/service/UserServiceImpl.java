package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (isDuplicateEmail(user.getEmail())) {
            throw new AlreadyExistsException(String.format("Email %s уже существует.", user.getEmail()));
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user, Long userId) {
        User updatedUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", user)));

        Set<String> emails = userRepository
                .findUsersByEmail(user.getEmail())
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());

        if (emails.contains(user.getEmail()) && (!user.getEmail().equals(updatedUser.getEmail()))) {
            throw new AlreadyExistsException(String.format("Email %s уже существует.", user.getEmail()));
        }
        if (user.getName() != null && !updatedUser.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updatedUser.getEmail().isBlank()) {
            updatedUser.setEmail(user.getEmail());
        }

        userRepository.save(updatedUser);
        return updatedUser;
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", id)));
    }

    @Override
    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    private boolean isDuplicateEmail(String userEmail) {
        Optional<String> email = userRepository.findUsersByEmail(userEmail).stream().map(User::getEmail).findAny();
        return email.isPresent();
    }
}
