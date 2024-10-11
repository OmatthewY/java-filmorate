package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAll();

    Film getById(Long id);

    void deleteAll();

    void deleteById(Long id);

    List<Film> getPopularFilms(Integer size);

}
