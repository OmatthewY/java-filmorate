package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorageImpl") UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User createUser(User user) {
        if (user == null) {
            log.info("Пользователь " + user.getId() + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (getUserById(user.getId()) == null) {
            log.info("Пользователь " + user.getId() + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        } else {
            return userStorage.updateUser(user);
        }
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public void deleteUserById(Long id) {
        if (getUserById(id) == null) {
            log.info("Пользователь " + id + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        userStorage.deleteUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
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

        friendshipStorage.addUserToFriends(userId, friendId);
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

        friendshipStorage.deleteUserFromFriends(userId, friendId);
        log.info("Удалили друга " + friendId + " у пользователя " + userId);
    }

    public List<User> getFriends(Long userId) {
        if (getUserById(userId) == null) {
            log.info("Пользователь " + userId + " не найден");
            throw new ConditionsNotMetException("Пользователь отсуствует в БД");
        } else {
            return friendshipStorage.getFriends(userId);
        }
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        List<User> user1List = friendshipStorage.getFriends(user1Id);
        List<User> user2List = friendshipStorage.getFriends(user2Id);

        List<User> commonFriends = new ArrayList<>();

        for (User userInList : getAllUsers()) {
            if (user1List.contains(userInList)
                    && user2List.contains(userInList)) {
                commonFriends.add(userInList);
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
