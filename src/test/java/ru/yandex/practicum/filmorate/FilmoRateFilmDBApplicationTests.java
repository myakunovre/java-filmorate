package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class})
class FilmoRateFilmDBApplicationTests {
    private final FilmRepository filmRepository;

    @Test
    public void testFindAllFilms() {
        List<Film> films = filmRepository.findAll();

        assertThat(films).isNotEmpty();

        assertThat(films).hasSize(4);

        List<Long> expectedIds = Arrays.asList(10001L, 10002L, 10003L, 10004L);

        assertThat(films).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmRepository.findById(10002);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 10002L)
                );
    }

    @Test
    public void createFilm() {
        Rating mpa = new Rating();
        mpa.setId(1);

        Film testFilm = new Film();

        testFilm.setName("Film Test");
        testFilm.setDescription("Description for Film Test");
        testFilm.setReleaseDate(LocalDate.now());
        testFilm.setDuration(180L);
        testFilm.setMpa(mpa);

        filmRepository.create(testFilm);

        List<Film> films = filmRepository.findAll();

        assertThat(films).anyMatch(film ->
                film.getId().equals(1L));

        assertThat(films).anyMatch(film ->
                film.getName().equals("Film Test"));

        assertThat(films).anyMatch(film ->
                film.getDescription().equals("Description for Film Test"));

        assertThat(films).anyMatch(film ->
                film.getReleaseDate().equals(LocalDate.now()));

        assertThat(films).anyMatch(film ->
                film.getDuration().equals(180L));

        assertThat(films).anyMatch(film ->
                film.getMpa().equals(mpa));
    }

    @Test
    public void updateFilm() {
        Rating testMpa = new Rating();
        testMpa.setId(1);

        Film testFilmUpdate = new Film();

        testFilmUpdate.setId(10002L);
        testFilmUpdate.setName("Film BB");
        testFilmUpdate.setDescription("Description for Film BB");
        testFilmUpdate.setReleaseDate(LocalDate.now());
        testFilmUpdate.setDuration(180L);
        testFilmUpdate.setMpa(testMpa);

        filmRepository.update(testFilmUpdate);

        Optional<Film> filmOptional = filmRepository.findById(10002L);

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("id", 10002L)
                );

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("name",
                                "Film BB")
                );

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("description",
                                "Description for Film BB")
                );

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                                LocalDate.now())
                );

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("duration",
                                180L)
                );

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("mpa",
                                testMpa)
                );
    }

    @Test
    public void addFilmLike() {
        filmRepository.addLike(10002L, 10002L);
        filmRepository.addLike(10002L, 10003L);
        filmRepository.addLike(10002L, 10004L);
        filmRepository.addLike(10003L, 10002L);
        filmRepository.addLike(10003L, 10003L);
        filmRepository.addLike(10004L, 10002L);

        List<Film> likedFilms = filmRepository.getPopular(3);

        List<Long> expectedIds = List.of(10004L, 10003L, 10002L);
        assertThat(likedFilms).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }
}