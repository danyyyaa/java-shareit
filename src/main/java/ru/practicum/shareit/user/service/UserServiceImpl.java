package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(String.format(
                    "Пользователь с %s уже зарегистрирован", user.getEmail()
            ));
        }
    }

    @Override
    public User update(User user, long userId) {
        User updatedUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", user)));

        if (user.getName() != null && !updatedUser.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updatedUser.getEmail().isBlank()) {
            updatedUser.setEmail(user.getEmail());
        }

        try {
            return userRepository.saveAndFlush(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(String.format(
                    "Пользователь с %s уже зарегистрирован", updatedUser.getEmail()
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }
}
