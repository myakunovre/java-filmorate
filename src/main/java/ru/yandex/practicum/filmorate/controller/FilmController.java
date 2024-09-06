package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {

        nullValidateBody(film);
        generalFilmValidate(film);

        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {

        nullValidateBody(newFilm);

        if (newFilm.getId() == null) {
            log.warn("Received Film object for updating without id");
            throw new ValidationException("Id должен быть указан");
        }

        generalFilmValidate(newFilm);

        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id,
                        @PathVariable long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable long id,
                           @PathVariable long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        if (count < 0) {
            throw new ValidationException("Параметр count не может быть отрицательным числом, а у Вас count = " + count);
        }
        return filmService.getPopular(count);
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
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
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
