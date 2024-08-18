package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

    FilmController filmController = new FilmController();
    UserController userController = new UserController();

    @Test
    void shouldAddFilm() {
        Film film = new Film();
        film.setName("Tittle");
        film.setDescription("Description with less than 200 chars for test");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120L);

        assertFalse(film.getName().isBlank() || film.getName().isEmpty(), "Название для теста д.б. не пустое");
        assertTrue(film.getDescription().length() <= 200, "Максимальная длина описания для теста д.б. 200 символов");
        assertTrue(film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28)),
                "Дата релиза для теста д.б. не раньше 28 декабря 1895 года");
        assertTrue(film.getDuration() > 0, "Продолжительность фильма д.б. положительным числом");

        filmController.add(film);

        assertEquals(1, filmController.findAll().size(), "Контроллер не добавил фильм, соответствующий " +
                "критериям проверки");
    }

    @Test
    void shouldThrowExceptionIfFilmNameIsEmpty() {
        Film film1 = new Film();
        film1.setName("");
        film1.setDescription("Description with less than 200 chars");
        film1.setReleaseDate(LocalDate.now());
        film1.setDuration(120L);

        assertTrue(film1.getName().isBlank() || film1.getName().isEmpty());

        try {
            filmController.add(film1);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер пропустил " +
                    "фильм с пустым названием (blank)");
        } catch (ValidationException e) {
            assertEquals("Название не может быть пустым", e.getMessage(), "Контроллер пропустил " +
                    "фильм с пустым названием (blank)");
        }

        Film film2 = new Film();
        film2.setDescription("Description with less than 200 chars");
        film2.setReleaseDate(LocalDate.now());
        film2.setDuration(120L);

        assertNull(film2.getName());

        try {
            filmController.add(film2);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер пропустил " +
                    "фильм без названия (null)");
        } catch (ValidationException e) {
            assertEquals("Название не может быть пустым", e.getMessage(), "Контроллер пропустил " +
                    "фильм без названия (null)");
        }
    }

    @Test
    void shouldThrowExceptionIfFilmDescriptionMoreThan200Chars() {
        Film film = new Film();
        film.setName("Tittle");
        film.setDescription("Description with more than 200 charssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120L);

        assertTrue(film.getDescription().length() > 200);

        try {
            filmController.add(film);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер пропустил фильм " +
                    "с длиной описания более 200 символов");
        } catch (ValidationException e) {
            assertEquals("Максимальная длина описания — 200 символов", e.getMessage(), "Контроллер " +
                    "пропустил фильм с длиной описания более 200 символов");
        }
    }

    @Test
    void shouldThrowExceptionIfFilmReleaseDateIsBeforeThan28Dec1985() {
        Film film = new Film();
        film.setName("Tittle");
        film.setDescription("Description with more than 200 chars");
        film.setReleaseDate(LocalDate.of(1985, 12, 27));
        film.setDuration(120L);

        assertTrue(film.getReleaseDate().isBefore(LocalDate.of(1985, 12, 28)));

        try {
            filmController.add(film);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер пропустил фильм " +
                    "с датой релиза раньше 28 декабря 1895 года");
        } catch (ValidationException e) {
            assertEquals("Дата релиза — не раньше 28 декабря 1895 года", e.getMessage(), "Контроллер " +
                    "пропустил фильм с датой релиза раньше 28 декабря 1895 года");
        }
    }

    @Test
    void shouldThrowExceptionIfFilmDurationIsNegative() {
        Film film = new Film();
        film.setName("Tittle");
        film.setDescription("Description with more than 200 chars");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-120L);

        assertTrue(film.getDuration() < 0);

        try {
            filmController.add(film);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер пропустил фильм, " +
                    "у которого продолжительность - отрицательное число");
        } catch (ValidationException e) {
            assertEquals("Продолжительность фильма должна быть положительным числом", e.getMessage(),
                    "Контроллер пропустил фильм, у которого продолжительность - отрицательное число");
        }
    }


    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("userLogin");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1984, 11, 16));

        assertFalse(user.getEmail().isBlank() || user.getEmail().isEmpty(), "Email для теста д.б. пустой");
        assertTrue(user.getEmail().contains("@"), "Email для теста должен содержать символ \"@\"");
        assertFalse(user.getLogin().isBlank() || user.getLogin().isEmpty(), "Логин для теста д.б. не пустой" +
                " или содержать пробелы");
        assertTrue(user.getBirthday().isBefore(LocalDate.now()), "Дата рождения для теста не может быть в будущем");

        userController.create(user);

        assertEquals(1, userController.findAll().size(), "Контроллер не создал пользователя, соответствующего " +
                "критериям проверки");
    }

    @Test
    void shouldThrowExceptionIfUserEmailIsEmpty() {
        User user1 = new User();
        user1.setEmail("");
        user1.setLogin("userLogin");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1984, 11, 16));

        assertTrue(user1.getEmail().isBlank() || user1.getEmail().isEmpty(), "Email для теста должен быть пустой");

        try {
            userController.create(user1);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер создал " +
                    "пользователя с пустым email (blank)");
        } catch (ValidationException e) {
            assertEquals("Электронная почта не может быть пустой", e.getMessage(), "Контроллер создал " +
                    "пользователя с пустым email (blank)");
        }

        User user2 = new User();
        user2.setLogin("userLogin");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1984, 11, 16));

        assertNull(user2.getEmail());

        try {
            userController.create(user2);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер создал " +
                    "пользователя с email = null");
        } catch (ValidationException e) {
            assertEquals("Электронная почта не может быть пустой", e.getMessage(), "Контроллер создал " +
                    "пользователя с email = null");
        }
    }

    @Test
    void shouldThrowExceptionIfUserEmailIsNotContainsGogChar() {
        User user = new User();
        user.setEmail("user-yandex.ru");
        user.setLogin("userLogin");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1984, 11, 16));

        assertFalse(user.getEmail().contains("@"), "Email для теста не должен содержать символ @");

        try {
            userController.create(user);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер создал " +
                    "пользователя с email без символа @");
        } catch (ValidationException e) {
            assertEquals("Электронная почта должна содержать символ @", e.getMessage(), "Контроллер создал " +
                    "пользователя с email без символа @");
        }
    }

    @Test
    void shouldThrowExceptionIfUserLoginIsEmptyOrContainsSpaces() {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1984, 11, 16));

        assertNull(user.getLogin());

        try {
            userController.create(user);
            assertNotEquals(1, userController.findAll().size(), "Контроллер создал " +
                    "пользователя без логина");
        } catch (ValidationException e) {
            assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage(), "Контроллер создал " +
                    "пользователя без логина");
        }

        user.setLogin("");
        assertTrue(user.getLogin().isBlank() || user.getLogin().isEmpty());

        try {
            userController.create(user);
            assertNotEquals(1, userController.findAll().size(), "Контроллер создал " +
                    "пользователя с пустым логином");
        } catch (ValidationException e) {
            assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage(), "Контроллер создал " +
                    "пользователя с пустым логином");
        }

        user.setLogin("user login");
        assertTrue(user.getLogin().contains(" "));

        try {
            userController.create(user);
            assertNotEquals(1, userController.findAll().size(), "Контроллер создал " +
                    "пользователя с логином, содержащим пробелы");
        } catch (ValidationException e) {
            assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage(), "Контроллер создал " +
                    "пользователя с логином, содержащим пробелы");
        }
    }

    @Test
    void shouldSetUserLoginAsNameIfNameIsEmpty() {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("userLogin");
        user.setBirthday(LocalDate.of(1984, 11, 16));

        assertNull(user.getName());

        userController.create(user);

        List<User> usersArray = new ArrayList<>(userController.findAll());
        String userName = usersArray.getFirst().getName();

        assertEquals(1, userController.findAll().size(), "Контроллер не создал " +
                "пользователя с пустым именем, а должен был");
        assertEquals("userLogin", userName, "В качестве имени должен быть использован логин, т.к. имя пустое");
    }

    @Test
    void shouldThrowExceptionIfUserBirthdayIsAfterThanNow() {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("userLogin");
        user.setName("UserName");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertTrue(user.getBirthday().isAfter(LocalDate.now()), "Дата рождения для теста должна быть в будущем");

        try {
            userController.create(user);
            assertNotEquals(1, filmController.findAll().size(), "Контроллер создал " +
                    "пользователя с датой рождения в будущем");
        } catch (ValidationException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage(), "Контроллер создал " +
                    "пользователя с датой рождения в будущем");
        }
    }
}
