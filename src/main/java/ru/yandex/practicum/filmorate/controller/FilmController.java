package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private long nextFilmId;
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@RequestBody Film newFilm) {
        validateFilm(newFilm);

        newFilm.setId(getNextFilmId());

        films.put(newFilm.getId(), newFilm);
        log.info("Фильм добавлен: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film updatedFilm) {
        if (!films.containsKey(updatedFilm.getId()) && updatedFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Film existingFilm = films.get(updatedFilm.getId());
        if (existingFilm == null) {
            throw new ConditionsNotMetException("Фильм с id = " + updatedFilm.getId() + " не найден");
        }

        validateFilm(updatedFilm);

        existingFilm.setName(updatedFilm.getName());
        existingFilm.setDescription(updatedFilm.getDescription());
        existingFilm.setReleaseDate(updatedFilm.getReleaseDate());
        existingFilm.setDuration(updatedFilm.getDuration());

        log.info("Фильм обновлен: {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Количество добавленных фильмов {}", films.size());
        return films.values();
    }

    private long getNextFilmId() {
        return ++nextFilmId;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }
}
