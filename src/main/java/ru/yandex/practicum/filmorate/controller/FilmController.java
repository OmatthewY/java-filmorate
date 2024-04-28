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
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@RequestBody Film newFilm) {
        if (newFilm.getName() == null || newFilm.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (newFilm.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (newFilm.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }

        newFilm.setId(getNextFilmId());

        films.put(newFilm.getId(), newFilm);
        log.info("Фильм добавлен: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film updatedFilm) {
        if (updatedFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Film existingFilm = films.get(updatedFilm.getId());

        if (existingFilm != null) {
            if (updatedFilm.getName() == null || updatedFilm.getName().isEmpty()) {
                throw new ValidationException("Название фильма не может быть пустым");
            } else {
                existingFilm.setName(updatedFilm.getName());
            }

            if (updatedFilm.getDescription().length() > 200) {
                throw new ValidationException("Максимальная длина описания — 200 символов");
            } else {
                existingFilm.setDescription(updatedFilm.getDescription());
            }

            if (updatedFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            } else {
                existingFilm.setReleaseDate(updatedFilm.getReleaseDate());
            }

            if (updatedFilm.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма не может быть отрицательной");
            } else {
                existingFilm.setDuration(updatedFilm.getDuration());
            }

            log.info("Фильм обновлен: {}", updatedFilm);
            return updatedFilm;
        }
        throw new ConditionsNotMetException("Фильм с id = " + updatedFilm.getId() + " не найден");
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private long getNextFilmId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
