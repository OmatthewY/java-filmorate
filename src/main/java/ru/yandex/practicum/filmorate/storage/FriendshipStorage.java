package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

    void addUserToFriends(Long user1Id, Long user2Id);

    void deleteUserFromFriends(Long user1Id, Long user2Id);

    List<User> getFriends(Long userId);
}
