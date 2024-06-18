package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.Update;
import ru.yandex.practicum.filmorate.validator.reliseDate.RealiseDateConstraint;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private static final int MAX_LENGTH_FILM_DESCRIPTION = 200;

    @NotNull(groups = Update.class)
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = MAX_LENGTH_FILM_DESCRIPTION, message = "Описание фильма не должно превышать 200 символов")
    private String description;

    @NotNull
    @RealiseDateConstraint
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    private Integer likesCount;
    private Set<Genre> genres;

    @NotNull
    private Mpa mpa;
}
