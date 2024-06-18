package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;

/**
 * User.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotNull(groups = Update.class)
    private Long id;

    @NotEmpty
    @Email(message = "Электронная почта должна содержать символ @")
    private String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "^\\S+$")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent(message = "Пользователь не мог родиться в будущем...или мог?!?!")
    private LocalDate birthday;
}
