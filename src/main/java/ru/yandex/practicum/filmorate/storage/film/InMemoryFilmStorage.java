package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {


    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());

        if (film.getUserLikes() == null) {
            film.setUserLikes(new HashSet<>());
        }

        films.put(film.getId(), film);

        log.info("Completed a new film add with the necessary parameters!");
        return film;
    }

    @Override
    public Film update(Film newFilm) throws NotFoundException {

        validateNotFound(newFilm.getId());

        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        log.info("Completed a new film update with the necessary parameters!");
        return oldFilm;
    }

    @Override
    public Film addLike(long id, long userId) throws NotFoundException {
        validateNotFound(id);
        inMemoryUserStorage.validateNotFound(userId);

        Film likedFilm = films.get(id);
        likedFilm.getUserLikes().add(userId);

        return likedFilm;
    }

    @Override
    public Film removeLike(long id, long userId) throws NotFoundException {
        validateNotFound(id);
        inMemoryUserStorage.validateNotFound(userId);

        Film unlikedFilm = films.get(id);
        unlikedFilm.getUserLikes().remove(userId);

        return unlikedFilm;
    }

    @Override
    public List<Film> getPopular(int count) {
        List<Film> sortedFilmsList = new ArrayList<>(films.values());

        sortedFilmsList.sort(
                new Comparator<Film>() {
                    @Override
                    public int compare(Film o1, Film o2) {
                        return o2.getUserLikes().size() - o1.getUserLikes().size();
                    }
                });

        if (count > films.size()) {
            count = films.size();
        }

        return IntStream.range(0, count)
                .mapToObj(sortedFilmsList::get)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateNotFound(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }
}
