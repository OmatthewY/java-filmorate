package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class GenreDbStorageImpl implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        try {
            String sql = "select id, genre_name from genre order by id";
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (Exception e) {
            log.error("Ошибка в получении списка жанров из БД: " + e.getMessage(), e);
            throw new ConditionsNotMetException("Ошибка в получении списка жанров из БД: ");
        }
    }

    @Override
    public Genre getById(Long id) {
        try {
            String sql = "select id, genre_name from genre where id = ? order by id";
            return jdbcTemplate.queryForObject(sql, this::mapRow, id);
        } catch (Exception e) {
            log.error("Ошибка в получении жанра по идентификатору из БД: " + e.getMessage(), e);
            throw new ConditionsNotMetException("Ошибка в получении жанра по идентификатору из БД: ");
        }
    }

    @Override
    public void loadGenresForFilm(List<Film> films) {
        try {
            final Map<Long, Film> filmById = films.stream()
                    .collect(Collectors.toMap(Film::getId, film -> film));

            String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

            final String sqlQuery = "select g.id as genre_id, g.genre_name, fg.film_id " +
                    "from genre g " +
                    "join film_genre fg on fg.genre_id = g.id " +
                    "where fg.film_id in (" + inSql + ")";

            jdbcTemplate.query(sqlQuery, (rs) -> {
                Long filmId = rs.getLong("film_id");
                Genre genre = mapRow(rs, 0);

                Film film = filmById.get(filmId);
                if (film != null) {
                    if (film.getGenres() == null) {
                        film.setGenres(new HashSet<>());
                    }
                    film.getGenres().add(genre);
                }
            }, films.stream().map(Film::getId).toArray());
        } catch (Exception e) {
            log.error("Ошибка в загрузке жанров для фильма из БД: " + e.getMessage(), e);
            throw new ConditionsNotMetException("Ошибка в загрузке жанров для фильма из БД: ");
        }
    }

    private Genre mapRow(ResultSet rs, int rowNum) throws SQLException {

        Genre genre = new Genre();

        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("genre_name"));

        return genre;
    }
}
