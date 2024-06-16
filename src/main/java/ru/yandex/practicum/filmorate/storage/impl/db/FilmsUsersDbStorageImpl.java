package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.FilmsUsers;
import ru.yandex.practicum.filmorate.storage.FilmsUsersStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
@AllArgsConstructor
public class FilmsUsersDbStorageImpl implements FilmsUsersStorage  {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long getLikes(Long filmId) {
        try {
            String sql = "select user_id from films_users where film_id = ?";
            return (long) jdbcTemplate.query(sql, this::mapRow, filmId).size();
        } catch (Exception e) {
            log.error("Ошибка в получении лайков у фильма");
            throw new ConditionsNotMetException("Ошибка в получении лайков у фильма");
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        try {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films_users")
                    .usingColumns("film_id", "user_id");

            insert.execute(new MapSqlParameterSource("film_id", filmId)
                    .addValue("user_id", userId));
        } catch (Exception e) {
            log.error("Ошибка в добавлении лайка к фильму");
            throw new ConditionsNotMetException("Ошибка в добавлении лайка к фильму");
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        try {
            String sql = "delete from films_users where film_id = ? and user_id = ?";
            jdbcTemplate.update(sql, filmId, userId);
        } catch (Exception e) {
            log.error("Ошибка в удалении лайка к фильму");
            throw new ConditionsNotMetException("Ошибка в удалении лайка к фильму");
        }
    }

    private FilmsUsers mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmsUsers filmsUsers = new FilmsUsers();

        filmsUsers.setUserId(rs.getLong("user_id"));

        return filmsUsers;
    }
}
