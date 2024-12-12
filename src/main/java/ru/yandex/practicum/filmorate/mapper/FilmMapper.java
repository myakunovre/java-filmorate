package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static Film mapToFilm(Film request, Rating mpa, List<Genre> genres) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(mpa);
        film.setGenres(genres);

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setDuration(film.getDuration());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setMpa(film.getMpa());

        if (film.getGenres() == null) {
            return dto;
        }

        dto.setGenres(film.getGenres());
//        dto.setMpa(mpa);
//        dto.setGenres(genres);
        return dto;
    }

//    public static User updateUserFields(User user, UpdateUserRequest request) {
//        if (request.hasEmail()) {
//            user.setEmail(request.getEmail());
//        }
//        if (request.hasPassword()) {
//            user.setPassword(request.getPassword());
//        }
//        if (request.hasUsername()) {
//            user.setUsername(request.getUsername());
//        }
//        return user;
//    }
}