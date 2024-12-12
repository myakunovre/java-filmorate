package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserRepository {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<User> mapper;

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, mapper);
    }

    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbc.query(sql, mapper, email).stream().findFirst();
    }

    public User create(User user) {
        validateNameAndSetLoginAsName(user);
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        long id = insert(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User newUser) {
        validateNotFound(newUser.getId());
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        update(
                sql,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    public List<User> addFriend(long userId, long friendId) {
        validateNotFound(userId);
        validateNotFound(friendId);
        String sql = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
        jdbc.update(sql, userId, friendId);
        return findUserFriends(userId);
    }

    public List<User> removeFriend(long userId, long friendId) {
        validateNotFound(userId);
        validateNotFound(friendId);
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbc.update(sql, userId, friendId);
        return findUserFriends(userId);
    }

    public List<User> findUserFriends(long userId) {
        validateNotFound(userId);
        String sql = "SELECT u.* " +
                "FROM friends AS f " +
                "JOIN users AS u ON f.FRIEND_ID = u.ID " +
                "WHERE f.USER_ID = ?";
        return jdbc.query(sql, mapper, userId);
    }

    public List<User> findCommonFriends(long userId, long friendId) {
        String sql =
                "SELECT u.*" +
                        "FROM PUBLIC.USERS u " +
                        "JOIN PUBLIC.FRIENDS f1 ON u.ID = f1.FRIEND_ID " +
                        "JOIN PUBLIC.FRIENDS f2 ON u.ID = f2.FRIEND_ID " +
                        "WHERE f1.USER_ID = ? " +
                        "  AND f2.USER_ID = ?";
        return jdbc.query(sql, mapper, userId, friendId);
    }

    private long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        // Возвращаем id нового пользователя
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    private void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    private static void validateNameAndSetLoginAsName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Received User object without name, setting login {} as user name", user.getLogin());

            user.setName(user.getLogin());
            log.trace("Received login \"{}\" as user name for user with id = {}", user.getLogin(), user.getId());
        }
    }

    private void validateNotFound(long id) {
        findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }
}
