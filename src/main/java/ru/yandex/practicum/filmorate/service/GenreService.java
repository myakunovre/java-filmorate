package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public Collection<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDto findById(int genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + genreId + " не найден"));

        return GenreMapper.mapToGenreDto(genre);
    }
}
