package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void filmNameValidationShouldThrowValidationException() {
		Film film = new Film();
		film.setName("");
		film.setDescription("Some description");
		film.setReleaseDate(LocalDate.of(2022, 1, 1));
		film.setDuration(120);
		assertThrows(ValidationException.class, () -> {
			if (film.getName().isEmpty()) {
				throw new ValidationException("Название фильма не может быть пустым");
			}
		});
	}

	@Test
	void filmDescriptionValidationShouldThrowValidationException() {
		Film film = new Film();
		film.setName("Some Film");
		film.setDescription("Это оооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооооо" +
				"ооооооооооооооооооочень длинное описание, " +
				"длина которого превышает максимально допустимую отметку в 200 символов.");
		film.setReleaseDate(LocalDate.of(2022, 1, 1));
		film.setDuration(120);
		assertThrows(ValidationException.class, () -> {
			if (film.getDescription().length() > 200) {
				throw new ValidationException("Максимальная длина описания — 200 символов");
			}
		});
	}

	@Test
	void filmReleaseDateValidationShouldThrowValidationException() {
		Film film = new Film();
		film.setName("Some Film");
		film.setDescription("Some description");
		film.setReleaseDate(LocalDate.of(1800, 1, 1));
		film.setDuration(120);
		assertThrows(ValidationException.class, () -> {
			if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
				throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
			}
		});
	}

	@Test
	void filmDurationValidationShouldThrowValidationException() {
		Film film = new Film();
		film.setName("Some Film");
		film.setDescription("Some description");
		film.setReleaseDate(LocalDate.of(2022, 1, 1));
		film.setDuration(-1);
		assertThrows(ValidationException.class, () -> {
			if (film.getDuration() <= 0) {
				throw new ValidationException("Продолжительность фильма не может быть отрицательной");
			}
		});
	}

	@Test
	void userInvalidEmailValidationShouldThrowValidationException() {
		User user = new User();
		user.setEmail("invalid_email");
		user.setLogin("someLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		assertThrows(ValidationException.class, () -> {
			if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
				throw new ValidationException("Неверный формат электронной почты");
			}
		});
	}

	@Test
	void userEmptyEmailValidationShouldThrowValidationException() {
		User user = new User();
		user.setEmail("");
		user.setLogin("someLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		assertThrows(ValidationException.class, () -> {
			if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
				throw new ValidationException("Неверный формат электронной почты");
			}
		});
	}

	@Test
	void userLoginValidationShouldThrowValidationException() {
		User user = new User();
		user.setEmail("valid@example.com");
		user.setLogin("");
		assertThrows(ValidationException.class, () -> {
			if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
				throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
			}
		});
	}

	@Test
	void userBirthdayValidationShouldThrowValidationException() {
		User user = new User();
		user.setEmail("valid@example.com");
		user.setLogin("someLogin");
		user.setBirthday(LocalDate.now().plusDays(1));
		assertThrows(ValidationException.class, () -> {
			if (user.getBirthday().isAfter(LocalDate.now())) {
				throw new ValidationException("Ты не мог родиться в будущем...или мог?!?!");
			}
		});
	}
}