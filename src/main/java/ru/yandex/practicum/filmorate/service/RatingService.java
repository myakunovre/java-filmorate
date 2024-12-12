package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public Collection<RatingDto> findAll() {
        return ratingRepository.findAll().stream()
                .map(RatingMapper::mapToRatingDto)
                .toList();
    }

    public RatingDto findById(int ratingId) {
        Rating mpa = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("MPA с id = " + ratingId + " не найден"));

        return RatingMapper.mapToRatingDto(mpa);
    }
}
