package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Starting a new user create!");

        nullValidateBody(user);
        log.trace("Completed User object validation for the null value for creating");

        generalUserValidate(user);
        log.trace("Completed User object general validation for create");

        user.setId(getNextId());
        log.trace("Has been set new id = {} to new User object", user.getId());

        validateNameAndSetLoginAsName(user);
        log.trace("Completed User object name validation for create");

        users.put(user.getId(), user);
        log.trace("Created new user with login \"{}\"", user.getLogin());
        log.info("Completed a new user create!");

        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Starting user update!");

        nullValidateBody(newUser);
        log.trace("Completed User object validation for the null value for updating");

        if (newUser.getId() == null) {
            log.warn("Received User object for updating without id");
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            log.trace("Received User object with correct id");

            generalUserValidate(newUser);
            log.trace("Completed User object general validation for update");

            List<String> userEmails = users.values().stream()
                    .map(User::getEmail)
                    .toList();
            log.trace("Got list of User emails to check for duplicate");

            if (userEmails.contains(newUser.getEmail())) {
                log.warn("Received User object with email {} which is already taken by another user", newUser.getEmail());
                throw new ValidationException("Этот email уже используется");
            }

            User oldUser = users.get(newUser.getId());
            log.trace("Got User object for update");

            oldUser.setEmail(newUser.getEmail());
            log.trace("Updated user name");

            oldUser.setLogin(newUser.getLogin());
            log.trace("Updated user email");

            oldUser.setName(newUser.getName());
            log.trace("Updated user name");

            oldUser.setBirthday(newUser.getBirthday());
            log.trace("Updated user birthday");

            validateNameAndSetLoginAsName(oldUser);
            log.trace("Completed User object name validation for update");

            return oldUser;
        }

        log.warn("Received User object for updating with missing id = {}", newUser.getId());
        throw new NotFoundException("Юзер с id = " + newUser.getId() + " не найден");
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
            throw new NotFoundException("Метод PUT должен передавать объект класса User");
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