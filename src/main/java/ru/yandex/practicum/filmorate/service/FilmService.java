package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {

    public final FilmStorage inMemoryFilmStorage;

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    public Film create(Film film) {
        return inMemoryFilmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return inMemoryFilmStorage.update(newFilm);
    }

    public Film addLike(long id, long userId) throws NotFoundException {

        return inMemoryFilmStorage.addLike(id, userId);
    }

    public Film removeLike(long id, long userId) throws NotFoundException {

        return inMemoryFilmStorage.removeLike(id, userId);
    }

    public List<Film> getPopular(int count) {

        return inMemoryFilmStorage.getPopular(count);
    }
}
