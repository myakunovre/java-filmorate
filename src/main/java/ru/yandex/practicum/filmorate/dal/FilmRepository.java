package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class FilmRepository {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<Film> mapper;
    private RatingRepository ratingRepository;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbc.query(sql, mapper);
    }

    public Optional<Film> findById(long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }

    public Film create(Film film) {
        String sql1 = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        long filmId = insert(
                sql1,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(filmId);

        if (film.getGenres() == null) {
            log.info("Completed a new film add with the necessary parameters!");
            return film;
        }

        String sql2 = "INSERT INTO film_genre(film_id, genre_id) " +
                "VALUES (?, ?)";

        List<Integer> genreIds = film.getGenres()
                .stream()
                .map(Genre::getId)
                .toList();

        genreIds.forEach(genreId -> jdbc.update(sql2, filmId, genreId));

        log.info("Completed a new film add with the necessary parameters!");
        return film;
    }

    public Film update(Film film) {
        validateNotFound(film.getId());

        String sqlUpdate = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "rating_id = ? WHERE id = ?";
        update(
                sqlUpdate,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
//                film.getGenres(),
                film.getId()
        );

        log.info("Completed a new film update with the necessary parameters!");
        return film;
    }

    public Film addLike(Long filmId, Long userId) {
        validateNotFound(filmId);

        String sql = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbc.update(sql, filmId, userId);
        return findById(filmId).get();
    }

    public Film removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
        return findById(filmId).get();
    }

    public List<Film> getPopular(int count) {
        String sql = "SELECT F.* " +
                "FROM PUBLIC.FILMS F " +
                "LEFT JOIN PUBLIC.LIKES L ON F.ID = L.FILM_ID " +
                "GROUP BY F.ID, F.NAME " +
                "ORDER BY COUNT(L.USER_ID) DESC " +
                "LIMIT ?";
        return jdbc.query(sql, mapper, count);
    }

    private void validateNotFound(Long id) {
        List<Long> filmIds = findAll()
                .stream()
                .map(Film::getId)
                .toList();
        if (!filmIds.contains(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    private long insert(String sql, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
}
