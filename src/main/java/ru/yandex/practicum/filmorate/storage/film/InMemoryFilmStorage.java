package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long nextFilmId;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film newFilm) {
        newFilm.setId(getNextFilmId());

        films.put(newFilm.getId(), newFilm);

        log.info("Фильм добавлен: {}", newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        if (!films.containsKey(updatedFilm.getId())) {
            log.info("Фильм отсутсвует в списке");
            throw new ConditionsNotMetException("Фильм отсуствует в списке");
        }
        films.put(updatedFilm.getId(), updatedFilm);

        log.info("Фильм обновлен: {}", updatedFilm);
        return updatedFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Количество добавленных фильмов {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public void deleteFilm(Film film) {
        log.info("Фильм под идентификатором - " + film.getId() + " удален");
        films.remove(film.getId());
    }

    private long getNextFilmId() {
        return ++nextFilmId;
    }
}
