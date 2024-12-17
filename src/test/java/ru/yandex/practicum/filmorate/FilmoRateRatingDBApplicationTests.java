package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({RatingRepository.class})
class FilmoRateRatingDBApplicationTests {
    private final RatingRepository ratingRepository;

    @Test
    public void testFindAllRatings() {

        List<Rating> ratings = ratingRepository.findAll();

        // Убедимся, что коллекция не пустая
        assertThat(ratings).isNotEmpty();

        // Проверка количества элементов в коллекции
        assertThat(ratings).hasSize(5);

        // Или если ожидаете конкретный список пользователей
        List<Integer> expectedIds = Arrays.asList(1, 2, 3, 4, 5);
        assertThat(ratings).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    public void testFindGenreById() {

        Optional<Rating> rating = ratingRepository.findById(2);

        assertThat(rating.get().getId()).isEqualTo(2);
    }
}