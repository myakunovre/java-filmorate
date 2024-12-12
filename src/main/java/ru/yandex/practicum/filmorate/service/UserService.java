package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
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
        User user1 = userRepository.create(user);

        return UserMapper.mapToUserDto(user1);
    }

    public UserDto update(User newUser) {
        User user = userRepository.update(newUser);
        return UserMapper.mapToUserDto(user);
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
}
