package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User create(User user) {
        if (user == null) {
            log.info("Пользователь не найден. Id Пользователя = {}", user.getId());
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        if (getById(user.getId()) == null) {
            log.info("Не удалось обновить пользователя по указанному Id. Указанный Id = {}", user.getId());
            throw new ConditionsNotMetException("Пользователь не найден");
        } else {
            return userStorage.update(user);
        }
    }

    public void deleteAll() {
        userStorage.deleteAll();
    }

    public void deleteById(Long id) {
        if (getById(id) == null) {
            log.info("Не удалось удалить пользователя по указанному Id. Указанный Id = {}", id);
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        userStorage.deleteById(id);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id);
    }

    public void addFriend(Long userId, Long friendId) {

        if (getById(userId) == null) {
            log.info("Не удалось найти пользователя (при добавлении друга) по Id. Id Пользователя = {}", userId);
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        if (getById(friendId) == null) {
            log.info("Не удалось найти друга пользователя (при добавлении друга) по Id. Id Друга пользователя = {}",
                    friendId);
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        friendshipStorage.addToFriends(userId, friendId);
        log.info("Добавили друга {} пользователю {}", friendId, userId);
    }

    public void deleteFriend(Long userId, Long friendId) {

        if (getById(userId) == null) {
            log.info("Не удалось найти пользователя (при удалении друга) по Id. Id Пользователя = {}", userId);
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        if (getById(friendId) == null) {
            log.info("Не удалось найти друга пользователя (при удалении друга) по Id. Id Друга пользователя = {}",
                    friendId);
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        friendshipStorage.deleteFromFriends(userId, friendId);
        log.info("Удалили друга {} у пользователя {}", friendId, userId);
    }

    public List<User> getFriends(Long userId) {
        if (getById(userId) == null) {
            log.info("Не удалось найти пользователя (при поиске друзей) по Id. Id Пользователя = {}", userId);
            throw new ConditionsNotMetException("Пользователь отсуствует в БД");
        } else {
            return friendshipStorage.getFriends(userId);
        }
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        List<User> commonFriends = friendshipStorage.getCommonFriends(user1Id, user2Id);
        log.info("Список общих друзей у пользователя " + user1Id + " и " + user2Id + " - {}", commonFriends);
        return commonFriends;
    }
}
