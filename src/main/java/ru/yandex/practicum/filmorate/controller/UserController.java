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
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@RequestBody User newUser) {
        if (newUser.getEmail() == null || newUser.getEmail().isEmpty() || !newUser.getEmail().contains("@")) {
            throw new ValidationException("Неверный формат электронной почты");
        }

        if (newUser.getLogin() == null || newUser.getLogin().isEmpty() || newUser.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        }

        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            newUser.setName(newUser.getLogin());
        }

        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ты не мог родиться в будущем...или мог?!?!");
        }

        newUser.setId(getNextUserId());

        users.put(newUser.getId(), newUser);
        log.info("Пользователь создан: {}", newUser);
        return newUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User existingUser = users.get(updatedUser.getId());

        if (existingUser != null) {
            if (updatedUser.getEmail() == null || updatedUser.getEmail().isEmpty() ||
                    !updatedUser.getEmail().contains("@")) {
                throw new ValidationException("Неверный формат электронной почты");
            } else {
                existingUser.setEmail(updatedUser.getEmail());
            }

            if (updatedUser.getLogin() == null || updatedUser.getLogin().isEmpty() ||
                    updatedUser.getLogin().contains(" ")) {
                throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
            } else {
                existingUser.setLogin(updatedUser.getLogin());
            }

            if (updatedUser.getName() == null || updatedUser.getName().isEmpty()) {
                updatedUser.setName(updatedUser.getLogin());
            } else {
                existingUser.setName(updatedUser.getName());
            }

            if (updatedUser.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Ты не мог родиться в будущем...или мог?!?!");
            } else {
                existingUser.setBirthday(updatedUser.getBirthday());
            }

            log.info("Пользователь обновлен: {}", updatedUser);
            return updatedUser;
        }
        throw new ConditionsNotMetException("Пользователь с id = " + updatedUser.getId() + " не найден");
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private long getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
