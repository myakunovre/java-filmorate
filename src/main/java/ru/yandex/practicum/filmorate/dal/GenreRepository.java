package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreRepository {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<Genre> mapper;

    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        return jdbc.query(sql, mapper);
    }

    public Optional<Genre> findById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }

    public List<Genre> findByFilmId(long filmId) {
        String sql = """
                SELECT g.*
                FROM film_genre fg
                LEFT JOIN genres g ON fg.genre_id = g.id
                WHERE fg.film_id = ?""";
        List<Genre> genres = jdbc.query(sql, mapper, filmId);
        return genres;
    }

    public List<Genre> findByIds(List<Integer> ids) {
        String sql = "SELECT * FROM genres WHERE id IN (" +
                ids.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";
        return jdbc.query(sql, mapper, ids.toArray());
    }
}
