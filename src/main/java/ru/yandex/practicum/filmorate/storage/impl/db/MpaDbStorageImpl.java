package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class MpaDbStorageImpl implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        try {
            String sql = "select id, mpa_name from mpa order by id";
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (Exception e) {
            log.error("Ошибка в получении списка mpa из БД: " + e.getMessage(), e);
            throw new ConditionsNotMetException("Ошибка в получении списка mpa из БД: ");
        }
    }

    @Override
    public Mpa getById(Long id) {
        try {
            String sql = "select id, mpa_name from mpa where id = ? order by id";
            return jdbcTemplate.queryForObject(sql, this::mapRow, id);
        } catch (Exception e) {
            log.error("Ошибка в получении mpa по идентификатору из БД: " + e.getMessage(), e);
            throw new ConditionsNotMetException("Ошибка в получении mpa по идентификатору из БД: ");
        }
    }

    private Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {

        Mpa mpa = new Mpa();

        mpa.setId(rs.getLong("id"));
        mpa.setName(rs.getString("mpa_name"));

        return mpa;
    }
}
