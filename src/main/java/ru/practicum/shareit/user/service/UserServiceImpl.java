package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+\\..+$");

    @Override
    public UserDto createUser(UserDto userDto) {
        if (isDuplicateEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException(String.format("Email %s уже существует.", userDto.getEmail()));
        }

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new UserValidationException("Email не может быть пустым.");
        }

        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
            throw new UserValidationException("Некорректный email");
        }

        User user = userRepository.createUser(UserMapper.mapToUser(userDto)).orElseThrow(() ->
                new UserAlreadyExistsException("Такой пользователь уже существует."));

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь %s не найден.", userId)));

        Set<String> emails = userRepository.getUsers().stream().map(User::getEmail).collect(Collectors.toSet());

        if (emails.contains(userDto.getEmail()) && (!userDto.getEmail().equals(user.getEmail()))) {
                throw new EmailAlreadyExistsException(String.format("Email %s уже существует.", userDto.getEmail()));
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        Optional<User> updatedUser = userRepository.updateUser(user, userId);
        assert updatedUser.isPresent();
        return UserMapper.mapToUserDto(updatedUser.get());
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.getUserById(id).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь %s не найден.", id)));

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository
                .getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toSet());
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
