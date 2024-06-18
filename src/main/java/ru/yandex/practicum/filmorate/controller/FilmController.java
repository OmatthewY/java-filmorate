package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.Update;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Validated(Update.class) @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("{id}")
    public Film getById(@PathVariable Long id) {
        return filmService.getById(id);
    }

    @DeleteMapping
    public void deleteAll() {
        filmService.deleteAll();
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        filmService.deleteById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @GetMapping("/{id}/like")
    public Long getLikes(@PathVariable Long filmId) {
        return filmService.getLikes(filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10",
            required = false) @Positive(message = "Параметр count должен быть больше 0") Integer count) {
        return filmService.getPopularFilms(count);
    }
}
