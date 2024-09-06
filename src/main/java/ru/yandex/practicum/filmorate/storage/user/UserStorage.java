package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    Collection<User> addFriend(Long id, Long friendId);

    Collection<User> removeFriend(Long id, Long friendId);

    Collection<User> findUserFriends(long userId);

    Collection<User> findCommonFriends(long id, long otherId);

    void validateNotFound(Long id);
}
