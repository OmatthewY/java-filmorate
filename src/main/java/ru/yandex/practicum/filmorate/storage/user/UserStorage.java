package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> getAll();

    User getById(Long id);

    void deleteById(Long id);

    void deleteAll();

}
