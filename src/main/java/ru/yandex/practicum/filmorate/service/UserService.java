package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto findById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        return UserMapper.mapToUserDto(user);
    }

    public UserDto create(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        Optional<User> alreadyExistUser = userRepository.findByEmail(user.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }

        User user1 = validateNameAndSetLoginAsName(user);

        User user2 = userRepository.create(user);

        return UserMapper.mapToUserDto(user2);
    }

    public UserDto update(User newUser) {
        userRepository.findById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        User updatedUser = userRepository.update(newUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public List<UserDto> addFriend(Long userId, Long friendId) {
        return userRepository.addFriend(userId, friendId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> removeFriend(Long userId, Long friendId) {
        return userRepository.removeFriend(userId, friendId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> findUserFriends(long userId) {
        return userRepository.findUserFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> findCommonFriends(long user1Id, long user2Id) {
        return userRepository.findCommonFriends(user1Id, user2Id).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private static User validateNameAndSetLoginAsName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Received User object without name, setting login {} as user name", user.getLogin());

            user.setName(user.getLogin());
            log.trace("Received login \"{}\" as user name", user.getLogin());
        }
        return user;
    }
}
