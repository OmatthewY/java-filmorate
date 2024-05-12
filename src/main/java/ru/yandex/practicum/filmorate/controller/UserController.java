package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private void validateUser(User user) throws ValidationException {
        if (user == null) {
            log.info("Пустые поля пользователя");
            throw new ValidationException("Пустые поля пользователя");
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("У пользователя неккоретная почта");
            throw new ValidationException("Неверный формат электронной почты");
        }

        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.info("У пользователя неккоретный логин");
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Пользователь не указал имя, поэтому его имя стало логином");
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Пользователь не мог родиться в будущем...или мог?!?!");
            throw new ValidationException("У пользователя неккоректная дата рождения");
        }
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);
        return userService.createUser(user);
    }

    @PutMapping
    User updateUser(@RequestBody User user) {
        validateUser(user);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping
    public void deleteUser(User user) {
        userService.deleteUser(user);
    }

    @PutMapping("/{id}/friends/{friendsId}")
    public void addFriend(@RequestBody @PathVariable Long id, @RequestBody @PathVariable Long friendsId) {
        userService.addFriend(id, friendsId);
    }

    @DeleteMapping("/{id}/friends/{friendsId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendsId) {
        userService.deleteFriend(id, friendsId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
