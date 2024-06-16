package ru.yandex.practicum.filmorate.storage;

public interface FilmsUsersStorage {

    Long getLikes(Long filmId);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
