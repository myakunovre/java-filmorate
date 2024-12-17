package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreRepository.class})
class FilmoRateGenreDBApplicationTests {
    private final GenreRepository genreRepository;

    @Test
    public void testFindAllGenres() {

        List<Genre> genres = genreRepository.findAll();

        // Убедимся, что коллекция не пустая
        assertThat(genres).isNotEmpty();

        // Проверка количества элементов в коллекции
        assertThat(genres).hasSize(6);

        // Или если ожидаете конкретный список пользователей
        List<Integer> expectedIds = Arrays.asList(1, 2, 3, 4, 5, 6);
        assertThat(genres).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    public void testFindGenreById() {

        Optional<Genre> genre = genreRepository.findById(1);

        assertThat(genre.get().getId()).isEqualTo(1);
    }
}