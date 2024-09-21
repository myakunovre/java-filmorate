package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        validateNameAndSetLoginAsName(user);
        user.setId(getNextId());

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        users.put(user.getId(), user);

        log.info("Completed a new user create with the necessary parameters!");
        return user;
    }

    @Override
    public User update(User newUser) throws NotFoundException, ValidationException {

        validateNotFound(newUser.getId());

        if (!users.containsKey(newUser.getId())) {
            throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        User oldUser = users.get(newUser.getId());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());

        validateNameAndSetLoginAsName(oldUser);

        log.info("Completed user update with the necessary parameters!");
        return oldUser;
    }

    @Override
    public Collection<User> addFriend(Long id, Long friendId) throws NotFoundException {
        validateNotFound(id);
        validateNotFound(friendId);

        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);

        return new ArrayList<>(users.values());
    }

    @Override
    public Collection<User> removeFriend(Long id, Long friendId) throws NotFoundException {
        validateNotFound(id);
        validateNotFound(friendId);

        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);

        return new ArrayList<>(users.values());
    }

    @Override
    public Collection<User> findUserFriends(long userId) throws NotFoundException {
        validateNotFound(userId);

        return users.get(userId).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findCommonFriends(long id, long otherId) throws NotFoundException {
        validateNotFound(id);
        validateNotFound(otherId);

        Set<Long> userFriendIds = users.get(id).getFriends();
        Set<Long> otherUserFriendIds = users.get(otherId).getFriends();

        List<User> commonFriends = new ArrayList<>();
        for (Long userFriendId : userFriendIds) {
            for (Long otherUserFriendId : otherUserFriendIds) {
                if (userFriendId.equals(otherUserFriendId)) {
                    commonFriends.add(users.get(userFriendId));
                }
            }
        }
        return commonFriends;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private static void validateNameAndSetLoginAsName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Received User object without name, setting login {} as user name", user.getLogin());

            user.setName(user.getLogin());
            log.trace("Received login \"{}\" as user name for user with id = {}", user.getLogin(), user.getId());
        }
    }

    public void validateNotFound(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }
}
