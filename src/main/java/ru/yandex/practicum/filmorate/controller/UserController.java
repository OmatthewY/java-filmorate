package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private long nextUserId;
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@RequestBody User newUser) {
        validateUser(newUser);

        newUser.setId(getNextUserId());

        users.put(newUser.getId(), newUser);
        log.info("Пользователь создан: {}", newUser);
        return newUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User updatedUser) {
        if (!users.containsKey(updatedUser.getId()) && updatedUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User existingUser = users.get(updatedUser.getId());
        if (existingUser == null) {
            throw new ConditionsNotMetException("Пользователь с id = " + updatedUser.getId() + " не найден");
        }

        validateUser(updatedUser);

        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setLogin(updatedUser.getLogin());
        existingUser.setName(updatedUser.getName());
        existingUser.setBirthday(updatedUser.getBirthday());

        log.info("Пользователь обновлен: {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Количество пользователей {}", users.size());
        return users.values();
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Неверный формат электронной почты");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ты не мог родиться в будущем...или мог?!?!");
        }
    }

    private long getNextUserId() {
        return ++nextUserId;
    }
}
