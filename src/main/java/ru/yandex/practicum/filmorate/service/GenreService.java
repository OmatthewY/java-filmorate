package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getById(Long id) {
        return genreStorage.getById(id);
    }
}
