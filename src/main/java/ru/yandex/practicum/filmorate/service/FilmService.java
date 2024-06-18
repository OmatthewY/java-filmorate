package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmsUsersStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmsUsersStorage filmsUsersStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService,
                       FilmsUsersStorage filmsUsersStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmsUsersStorage = filmsUsersStorage;
        this.genreStorage = genreStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (getById(film.getId()) == null) {
            throw new ConditionsNotMetException("Фильм отсуствует в БД");
        } else {
            return filmStorage.update(film);
        }
    }

    public void deleteAll() {
        filmStorage.deleteAll();
    }

    public void deleteById(Long id) {
        filmStorage.deleteById(id);
    }

    public List<Film> getAll() {
        List<Film> films = filmStorage.getAll();
        genreStorage.loadGenresForFilm(films);
        return films;
    }

    public Film getById(Long id) {
        Film film = filmStorage.getById(id);
        genreStorage.loadGenresForFilm(List.of(film));
        return film;
    }

    public void addLike(Long filmId, Long userId) {

        if (userService.getById(userId) == null) {
            log.info("Не удалось найти пользователя (при добавлении лайка) по Id. Id Пользователя = {}", userId);
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        if (getById(filmId) == null) {
            log.info("Не удалось найти фильм (при добавлении лайка) по Id. Id Фильма = {}", filmId);
            throw new ConditionsNotMetException("Фильм не найден");
        }

        filmsUsersStorage.addLike(filmId, userId);
        log.info("Пользователь " + userId + " добавил лайк фильму " + filmId);
    }


    public void deleteLike(Long userId, Long filmId) {

        if (userService.getById(userId) == null) {
            log.info("Не удалось найти пользователя (при удалении лайка) по Id. Id Пользователя = {}", userId);
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        if (getById(filmId) == null) {
            log.info("Не удалось найти фильм (при удалении лайка) по Id. Id Фильма = {}", filmId);
            throw new ConditionsNotMetException("Фильм не найден");
        }

        filmsUsersStorage.deleteLike(filmId, userId);
        log.info("Пользователь " + userId + " удалил лайк к фильму " + filmId);
    }

    public Long getLikes(Long filmId) {
        return filmsUsersStorage.getLikes(filmId);
    }

    public List<Film> getPopularFilms(Integer size) {
        List<Film> popularFilms = filmStorage.getPopularFilms(size);

        if (popularFilms.isEmpty()) {
            log.info("Список фильмов пуст");
            throw new ConditionsNotMetException("Популярные фильмы не найдены");
        }

        return popularFilms;
    }
}
