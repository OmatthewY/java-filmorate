package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private long nextUserId;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User newUser) {
        newUser.setId(getNextUserId());

        users.put(newUser.getId(), newUser);

        log.info("Пользователь создан: {}", newUser);
        return newUser;
    }

    @Override
    public User updateUser(User updatedUser) {
        if (!users.containsKey(updatedUser.getId())) {
            log.info("Пользователь отсуствует в списке");
            throw new ConditionsNotMetException("Пользователь отсутствует в списке");
        }
        users.put(updatedUser.getId(), updatedUser);

        log.info("Пользователь обновлен: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Количество пользователей {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(User user) {
        log.info("Пользователь под идентификатором - " + user.getId() + " удален");
        users.remove(user.getId());
    }

    private long getNextUserId() {
        return ++nextUserId;
    }
}
