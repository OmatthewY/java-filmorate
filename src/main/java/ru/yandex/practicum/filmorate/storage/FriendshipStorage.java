package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

    void addToFriends(Long user1Id, Long user2Id);

    void deleteFromFriends(Long user1Id, Long user2Id);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long user1Id, Long user2Id);
}
