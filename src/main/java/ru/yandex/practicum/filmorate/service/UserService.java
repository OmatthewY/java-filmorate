package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage inMemoryUserStorage;

    public User createUser(User user) {
        if (user == null) {
            log.info("Пользователь " + user.getId() + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        return inMemoryUserStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (user == null) {
            log.info("Пользователь " + user.getId() + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        return inMemoryUserStorage.updateUser(user);
    }

    public void deleteUser(User user) {
        if (user == null) {
            log.info("Пользователь " + user.getId() + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        inMemoryUserStorage.deleteUser(user);
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return inMemoryUserStorage.getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {

        if (getUserById(userId) == null) {
            log.info("Пользователь под идентификатором - " + userId + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        if (getUserById(friendId) == null) {
            log.info("Пользователь под идентификатором - " + friendId + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Добавили друга " + friendId + " пользователю " + userId);
    }

    public void deleteFriend(Long userId, Long friendId) {

        if (getUserById(userId) == null) {
            log.info("Пользователь под идентификатором - " + userId + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        if (getUserById(friendId) == null) {
            log.info("Пользователь под идентификатором - " + friendId + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Удалили друга " + friendId + " у пользователя " + userId);
    }

    public List<User> getFriends(Long userId) {
        if (getUserById(userId) != null) {
            List<User> friendsList = new ArrayList<>();
            for (long friendId : getUserById(userId).getFriends()) {
                User friend = getUserById(friendId);
                friendsList.add(friend);
            }
            log.info("Список друзей у пользователя " + userId + " - " + friendsList);
            return friendsList;
        } else {
            log.info("Пользователь " + userId + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        User user = getUserById(user1Id);
        User user2 = getUserById(user2Id);
        List<User> commonFriends = new ArrayList<>();

        for (User userInList : getAllUsers()) {
            if (user.getFriends().contains(userInList.getId())
                    && user2.getFriends().contains(userInList.getId())) {
                commonFriends.add(getUserById(userInList.getId()));
            }
        }
        log.info("Список общих друзей у пользователя " + user1Id + " и " + user2Id + " - " + commonFriends);
        return commonFriends;
    }

    public void validateUser(User user) throws ValidationException {
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
}
