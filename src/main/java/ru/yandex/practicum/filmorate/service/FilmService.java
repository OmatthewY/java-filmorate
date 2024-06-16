package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.FilmsUsersDbStorageImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmsUsersDbStorageImpl filmsUsersDbStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorageImpl") FilmStorage filmStorage, UserService userService,
                       FilmsUsersDbStorageImpl filmsUsersDbStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmsUsersDbStorage = filmsUsersDbStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()) == null) {
            throw new ConditionsNotMetException("Фильм отсуствует в БД");
        } else {
            return filmStorage.updateFilm(film);
        }
    }

    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long filmId, Long userId) {

        if (userService.getUserById(userId) == null) {
            log.info("Пользователь под идентификатором - " + userId + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        if (getFilmById(filmId) == null) {
            log.info("Фильм под идентификатором - " + filmId + " не найден");
            throw new ConditionsNotMetException("Фильм не найден");
        }

        filmsUsersDbStorage.addLike(filmId, userId);
        log.info("Пользователь " + userId + " добавил лайк фильму " + filmId);
    }


    public void deleteLike(Long userId, Long filmId) {

        if (userService.getUserById(userId) == null) {
            log.info("Пользователь под идентификатором - " + userId + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        if (getFilmById(filmId) == null) {
            log.info("Фильм под идентификатором - " + filmId + " не найден");
            throw new ConditionsNotMetException("Фильм не найден");
        }

        filmsUsersDbStorage.deleteLike(filmId, userId);
        log.info("Пользователь " + userId + " удалил лайк к фильму " + filmId);
    }

    public Long getLikes(Long filmId) {
        return filmsUsersDbStorage.getLikes(filmId);
    }

    public List<Film> getPopularFilms(Integer size) {
        Comparator<Film> filmComparator = (o1, o2) -> o2.getLikesCount().compareTo(o1.getLikesCount());

        List<Film> popularFilms = new ArrayList<>(getAllFilms());

        if (popularFilms.isEmpty()) {
            log.info("Список фильмов пуст");
            throw new ConditionsNotMetException("Популярные фильмы не найдены");
        }

        if (size >= popularFilms.size()) {
            size = popularFilms.size();
            log.info("Размер списка популярных фильмов изменен на " + size);
        }

        return popularFilms.stream()
                .filter(film -> film.getLikesCount() != null)
                .sorted(filmComparator)
                .collect(Collectors.toList());
    }

    public void validateFilm(Film film) throws ValidationException {
        if (film == null) {
            log.info("Пустые поля фильма");
            throw new ValidationException("Пустые поля фильма");
        }

        if (film.getName() == null || film.getName().isEmpty()) {
            log.info("Название фильма не задано");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.info("Длина описания больше 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.info("Неккоректная дата релиза фильма");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.info("Неккоректная длительность фильма");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }
}
