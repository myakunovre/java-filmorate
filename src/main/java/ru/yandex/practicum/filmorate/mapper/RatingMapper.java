package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RatingMapper {
    public static Rating mapToRating(Rating request) {
        Rating mpa = new Rating();
        mpa.setName(request.getName());

        return mpa;
    }

    public static RatingDto mapToRatingDto(Rating mpa) {
        RatingDto dto = new RatingDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());

        return dto;
    }
}