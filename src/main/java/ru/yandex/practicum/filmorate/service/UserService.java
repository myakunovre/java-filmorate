package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService {

    public final UserStorage inMemoryUserStorage;

    public Collection<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    public User create(User user) {
        return inMemoryUserStorage.create(user);
    }

    public User update(User newUser) throws ValidationException {
        return inMemoryUserStorage.update(newUser);
    }

    public Collection<User> addFriend(Long id, Long friendId) throws NotFoundException {
        return inMemoryUserStorage.addFriend(id, friendId);
    }

    public Collection<User> removeFriend(Long id, Long friendId) throws NotFoundException {
        return inMemoryUserStorage.removeFriend(id, friendId);
    }

    public Collection<User> findUserFriends(long userId) throws NotFoundException {
        return inMemoryUserStorage.findUserFriends(userId);
    }

    public Collection<User> findCommonFriends(long id, long otherId) throws NotFoundException {
        return inMemoryUserStorage.findCommonFriends(id, otherId);
    }
}
