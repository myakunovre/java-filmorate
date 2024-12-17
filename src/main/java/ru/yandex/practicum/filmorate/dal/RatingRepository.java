package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RatingRepository {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<Rating> mapper;

    public List<Rating> findAll() {
        String sql = "SELECT * FROM ratings";
        return jdbc.query(sql, mapper);
    }

    public Optional<Rating> findById(int id) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }
}
