package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
            log.error("Ошибка в получении списка жанров из БД");
            throw new ConditionsNotMetException("Ошибка в получении списка жанров из БД");
        }
    }

    @Override
    public Genre getById(Long id) {
        try {
            String sql = "select id, genre_name from genre where id = ? order by id";
            return jdbcTemplate.queryForObject(sql, this::mapRow, id);
        } catch (Exception e) {
            log.error("Ошибка в получении жанра по идентификатору из БД");
            throw new ConditionsNotMetException("Ошибка в получении жанра по идентификатору из БД");
        }
    }

    private Genre mapRow(ResultSet rs, int rowNum) throws SQLException {

        Genre genre = new Genre();

        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("genre_name"));

        return genre;
    }
}
