package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class UserDbStorageImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        try {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingGeneratedKeyColumns("id");

            long userId = insert.executeAndReturnKey(new MapSqlParameterSource("name", user.getName())
                    .addValue("email", user.getEmail())
                    .addValue("login", user.getLogin())
                    .addValue("birthday", user.getBirthday())).longValue();

            user.setId(userId);

            if (user.getLikedFilms() != null && !user.getLikedFilms().isEmpty()) {
                SimpleJdbcInsert insert2 = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("films_users")
                        .usingColumns("film_id", "user_id");

                for (Long filmId : user.getLikedFilms()) {
                    insert2.execute(new MapSqlParameterSource("user_id", userId)
                            .addValue("film_id", filmId));
                }
            }

            if (user.getFriends() != null && !user.getFriends().isEmpty()) {
                SimpleJdbcInsert insert3 = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("friendship")
                        .usingColumns("user_id", "friend_id");

                for (Long friendId : user.getFriends()) {
                    insert3.execute(new MapSqlParameterSource("user1_id", userId)
                            .addValue("friend_id", friendId));
                }
            }
            return user;
        } catch (Exception e) {
            log.error("Ошибка в добавлении пользователя в БД: " + e.getMessage(), e);
            throw new IncorrectParameterException("Ошибка в добавлении пользователя в БД");
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            String sql = "update users set name = ?, email = ?, login = ?, birthday = ? where id = ?";

            jdbcTemplate.update(sql, user.getName(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getBirthday(),
                    user.getId());

            return user;
        } catch (Exception e) {
            log.error("Ошибка в обновлении пользователя в БД");
            throw new ConditionsNotMetException("Ошибка в обновлении пользователя в БД");
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            String sql = "select u.id, u.name, u.email, u.login, u.birthday, " +
                    "string_agg(fu.film_id, ',') as liked_films, string_agg(fr.friend_id, ',') as friends from users u " +
                    "left join films_users fu on u.id = fu.user_id " +
                    "left join friendship fr on u.id = fr.user_id " +
                    "group by u.id " +
                    "order by u.id";

            return jdbcTemplate.query(sql, this::mapRow);
        } catch (Exception e) {
            log.error("Ошибка в получении всех пользователей из БД");
            throw new ConditionsNotMetException("Ошибка в получении всех пользователей из БД");
        }
    }

    @Override
    public User getUserById(Long id) {
        try {
            String sql = "select u.id, u.name, u.email, u.login, u.birthday, " +
                    "string_agg(fu.film_id, ',') as liked_films, string_agg(fr.friend_id, ',') as friends from users u " +
                    "left join films_users fu on u.id = fu.user_id " +
                    "left join friendship fr on u.id = fr.user_id " +
                    "where u.id = ? " +
                    "group by u.id " +
                    "order by u.id";

            return jdbcTemplate.queryForObject(sql, this::mapRow, id);
        } catch (Exception e) {
            log.error("Ошибка в получении пользователя по идентификатору из БД");
            throw new ConditionsNotMetException("Ошибка в получении пользователя по идентификатору из БД");
        }
    }

    @Override
    public void deleteAllUsers() {
        try {
            String sql = "delete from users";
            jdbcTemplate.update(sql);
            String sql2 = "delete from films_users";
            jdbcTemplate.update(sql2);
            String sql3 = "delete from friendship";
            jdbcTemplate.update(sql3);
        } catch (Exception e) {
            log.error("Ошибка при удалении всех пользователей из БД");
            throw new ConditionsNotMetException("Ошибка при удалении всех пользователей из БД");
        }
    }

    @Override
    public void deleteUserById(Long id) {
        try {
            String sql = "delete from users where id = ?";
            jdbcTemplate.update(sql, id);
            String sql2 = "delete from films_users where user_id = ?";
            jdbcTemplate.update(sql2, id);
            String sql3 = "delete from friendship where user_id = ?";
            jdbcTemplate.update(sql3, id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя из БД");
            throw new ConditionsNotMetException("Ошибка при удалении пользователя из БД");
        }
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();

        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        String likedFilms = rs.getString("liked_films");

        if (likedFilms != null && !likedFilms.isEmpty()) {
            String[] ids = likedFilms.split(",");
            Set<Long> setId = new HashSet<>();

            for (String id : ids) {
                setId.add(Long.parseLong(id));
            }

            user.setLikedFilms(setId);
        }

        String friends = rs.getString("friends");

        if (friends != null && !friends.isEmpty()) {
            String[] friendIds = friends.split(",");
            Set<Long> setFriendsId = new HashSet<>();

            for (String id : friendIds) {
                setFriendsId.add(Long.parseLong(id));
            }

            user.setFriends(setFriendsId);
        }

        return user;
    }
}
