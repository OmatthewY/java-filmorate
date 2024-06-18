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
import java.util.List;
import java.util.Set;
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
            List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());

            String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

            final String sqlQuery = "select fg.film_id, g.id as genre_id, g.genre_name " +
                    "from film_genre fg " +
                    "join genre g on fg.genre_id = g.id " +
                    "where fg.film_id in (" + inSql + ")";

            jdbcTemplate.query(sqlQuery, filmIds.toArray(), rs -> {
                Long filmId = rs.getLong("film_id");
                Genre genre = mapRow(rs, 0);

                films.stream()
                        .filter(film -> film.getId().equals(filmId))
                        .findFirst()
                        .ifPresent(film -> {
                            Set<Genre> genres = film.getGenres();
                            genres.add(genre);
                            film.setGenres(genres);
                        });
            });
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
