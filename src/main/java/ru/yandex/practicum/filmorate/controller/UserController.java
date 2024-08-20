package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {

        nullValidateBody(user);
        generalUserValidate(user);

        user.setId(getNextId());
        validateNameAndSetLoginAsName(user);
        users.put(user.getId(), user);

        log.info("Completed a new user create with the necessary parameters!");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {

        nullValidateBody(newUser);

        if (newUser.getId() == null) {
            log.warn("Received User object for updating without id");
            throw new ValidationException("Id должен быть указан");
        }

        generalUserValidate(newUser);

        List<String> userEmails = users.values().stream().map(User::getEmail).toList();
        if (userEmails.contains(newUser.getEmail())) {
            log.warn("Received User object with email {} which is already taken by another user", newUser.getEmail());
            throw new ValidationException("Этот email уже используется");
        }

        User oldUser = users.get(newUser.getId());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());

        validateNameAndSetLoginAsName(oldUser);

        log.info("Completed user update with the necessary parameters!");
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private static void nullValidateBody(User user) {
        if (user == null) {
            log.warn("Request has not contain a body of User-class");
            throw new ValidationException("Метод PUT должен передавать объект класса User");
        }
    }

    private static void generalUserValidate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Received User object without email");
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Received User object with email not contains char '@'");
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Received User object without login or login contains char ' '");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {

            log.warn("Received User object with birthday {} later than date now {}",
                    user.getBirthday().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private static void validateNameAndSetLoginAsName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Received User object without name, setting login {} as user name", user.getLogin());

            user.setName(user.getLogin());
            log.trace("Received login \"{}\" as user name for user with id = {}", user.getLogin(), user.getId());
        }
    }
}