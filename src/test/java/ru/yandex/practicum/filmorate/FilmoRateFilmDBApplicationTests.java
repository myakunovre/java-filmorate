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

        assertThat(films).hasSize(3);

        List<Long> expectedIds = Arrays.asList(2L, 3L, 4L);

        assertThat(films).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmRepository.findById(2);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                );
    }

    @Test
    public void createFilm() {
        Rating mpa = new Rating();
        mpa.setId(1);

        Film testFilm = new Film();

        testFilm.setName("Film A");
        testFilm.setDescription("Description for Film A");
        testFilm.setReleaseDate(LocalDate.now());
        testFilm.setDuration(180L);
        testFilm.setMpa(mpa);

        filmRepository.create(testFilm);

        List<Film> films = filmRepository.findAll();

        assertThat(films).anyMatch(film ->
                film.getId().equals(1L));

        assertThat(films).anyMatch(film ->
                film.getName().equals("Film A"));

        assertThat(films).anyMatch(film ->
                film.getDescription().equals("Description for Film A"));

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

        testFilmUpdate.setId(2L);
        testFilmUpdate.setName("Film BB");
        testFilmUpdate.setDescription("Description for Film BB");
        testFilmUpdate.setReleaseDate(LocalDate.now());
        testFilmUpdate.setDuration(180L);
        testFilmUpdate.setMpa(testMpa);

        filmRepository.update(testFilmUpdate);

        Optional<Film> filmOptional = filmRepository.findById(2L);

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("id", 2L)
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
        filmRepository.addLike(2L, 2L);
        filmRepository.addLike(2L, 3L);
        filmRepository.addLike(2L, 4L);
        filmRepository.addLike(3L, 2L);
        filmRepository.addLike(3L, 3L);
        filmRepository.addLike(4L, 2L);

        List<Film> likedFilms = filmRepository.getPopular(3);

        List<Long> expectedIds = List.of(4L, 3L, 2L);
        assertThat(likedFilms).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }
}