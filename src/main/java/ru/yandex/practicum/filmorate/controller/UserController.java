package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        nullValidateBody(user);
        generalUserValidate(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        nullValidateBody(newUser);
        if (newUser.getId() == null) {
            log.warn("Received User object for updating without id");
            throw new ValidationException("Id должен быть указан");
        }
        generalUserValidate(newUser);
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Collection<User> addFriend(@PathVariable long id,
                                      @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Collection<User> removeFriend(@PathVariable long id,
                                         @PathVariable long friendId) {
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable long id) {
        return userService.findUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable long id,
                                              @PathVariable long otherId) {
        return userService.findCommonFriends(id, otherId);
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
}