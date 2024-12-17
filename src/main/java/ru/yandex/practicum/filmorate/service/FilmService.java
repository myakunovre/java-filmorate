package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final RatingRepository ratingRepository;
    private final GenreRepository genreRepository;

    public Collection<FilmDto> findAll() {
        return filmRepository.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto findById(long filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));

        Rating mpa = getRating(film);
        film.setMpa(mpa);

        List<Genre> genres = genreRepository.findByFilmId(filmId);

        if (genres.isEmpty()) {
            return FilmMapper.mapToFilmDto(film);
        }

        film.setGenres(genres);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto create(Film newFilm) {
        Rating mpa = getRating(newFilm);

        List<Genre> genres = null;
        if (newFilm.getGenres() != null) {
            genres = getGenres(newFilm);
        }

        Film film = FilmMapper.mapToFilm(newFilm, mpa, genres);
        Film filmWithID = filmRepository.create(film);

        return FilmMapper.mapToFilmDto(filmWithID);
    }

    public FilmDto update(Film updateFilm) {
        filmRepository.update(updateFilm);
        Film film = filmRepository.findById(updateFilm.getId()).get();

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto addLike(long filmId, long userId) {
        Film film = filmRepository.addLike(filmId, userId);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto removeLike(long id, long userId) {
        Film film = filmRepository.removeLike(id, userId);

        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> getPopular(int count) {
        return filmRepository.getPopular(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private Rating getRating(Film film) {
        Rating mpa = ratingRepository.findById(film.getMpa().getId())
                .orElseThrow(() -> new ValidationException("MPA с id = " + film.getMpa().getId() + " не найден"));
        return mpa;
    }

    private List<Genre> getGenres(Film film) {
        List<Integer> filmGenreIds = film.getGenres().stream()
                .map(Genre::getId)
                .toList();

        List<Integer> allGenreIds = genreRepository.findAll().stream()
                .map(Genre::getId)
                .toList();

        filmGenreIds.stream()
                .filter(filmGenreId -> !allGenreIds.contains(filmGenreId))
                .forEach(filmGenreId -> {
                    throw new ValidationException("Жанр с id = " + filmGenreId + " не найден");
                });

        return genreRepository.findByIds(filmGenreIds);
    }
}
