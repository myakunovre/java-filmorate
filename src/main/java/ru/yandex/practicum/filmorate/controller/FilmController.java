package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Starting a new film add!");

        nullValidateBody(film);
        log.trace("Completed Film object validation for the null value for adding");

        generalFilmValidate(film);
        log.trace("Completed Film object validation for add");

        film.setId(getNextId());
        log.trace("Has been set new id = {} to new Film object", film.getId());

        films.put(film.getId(), film);
        log.trace("Added new film \"{}\"", film.getName());
        log.info("Completed a new film add!");

        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Starting film update!");

        nullValidateBody(newFilm);
        log.trace("Completed Film object validation for the null value for updating");

        if (newFilm.getId() == null) {
            log.warn("Received Film object for updating without id");
            throw new ValidationException("Id должен быть указан");
        }

        generalFilmValidate(newFilm);
        log.trace("Completed Film object validation for update");

        Film oldFilm = films.get(newFilm.getId());
        log.trace("Received Film object for update");

        oldFilm.setName(newFilm.getName());
        log.trace("Updated film name");

        oldFilm.setDescription(newFilm.getDescription());
        log.trace("Updated film description");

        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        log.trace("Updated film release date");

        oldFilm.setDuration(newFilm.getDuration());
        log.trace("Updated film duration");
        log.info("Completed a new film update!");

        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private static void nullValidateBody(Film film) {
        if (film == null) {
            log.warn("Request has not contain a body of Film-class");
            throw new ValidationException("Метод PUT должен передавать объект класса Film");
        }
    }

    private static void generalFilmValidate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Received Film object without name");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Received Film object with description length = {} chars, max length should not exceed 200 chars",
                    film.getDescription().length());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1985, 12, 28))) {
            log.warn("Received Film object with release date of {}, release date should not be early than December 28, 1895",
                    film.getReleaseDate().format(DateTimeFormatter.ofPattern("LL dd, yyyy")));
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Received Film object with duration of {}, duration should not be negative",
                    film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
