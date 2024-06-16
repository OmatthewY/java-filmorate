package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage  {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addUserToFriends(Long userId, Long friendId) {
        try {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("friendship")
                    .usingColumns("user_id", "friend_id");

            insert.execute(new MapSqlParameterSource("user_id", userId)
                    .addValue("friend_id", friendId));
        } catch (Exception e) {
            log.error("Ошибка в добавлении друга к пользователю");
            throw new ConditionsNotMetException(("Ошибка в добавлении друга к пользователю"));
        }
    }

    @Override
    public void deleteUserFromFriends(Long userId, Long friendId) {
        try {
            String sql = "delete from friendship where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId);
        } catch (Exception e) {
            log.error("Ошибка в удалении друга у пользователя");
            throw new ConditionsNotMetException("Ошибка в удалении друга у пользователя");
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        try {
            String sql = "select u.* from users u join friendship fr on u.id = fr.friend_id where fr.user_id = ?";
            return jdbcTemplate.query(sql, this::mapRow, userId);
        } catch (Exception e) {
            log.error("Ошибка в получении списка друзей пользователя");
            throw new ConditionsNotMetException("Ошибка в получении списка друзей пользователя");
        }
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }
}
