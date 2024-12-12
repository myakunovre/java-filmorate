package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, FilmRepository.class})
class FilmoRateUserDBApplicationTests {
    private final UserRepository userRepository;

    @Test
    public void testFindAllUsers() {
        List<User> users = userRepository.findAll();

        // Убедимся, что коллекция не пустая
        assertThat(users).isNotEmpty();

        // Проверка количества элементов в коллекции
        assertThat(users).hasSize(5);

        // Или если ожидаете конкретный список пользователей
        List<Long> expectedIds = Arrays.asList(2L, 3L, 4L, 5L, 6L);
        assertThat(users).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userRepository.findById(2);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                );
    }

    @Test
    public void findUserByEmail() {
        Optional<User> userOptional = userRepository.findByEmail("user2@example.com");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                );
    }

    @Test
    public void createUser() {
        User testUser = new User();
        testUser.setEmail("user1@example.com");
        testUser.setLogin("user1");
        testUser.setName("User One");
        testUser.setBirthday(LocalDate.now());

        userRepository.create(testUser);

        List<User> users = userRepository.findAll();

        assertThat(users).anyMatch(user ->
                user.getId().equals(1L));

        assertThat(users).anyMatch(user ->
                user.getEmail().equals("user1@example.com"));

        assertThat(users).anyMatch(user ->
                user.getLogin().equals("user1"));

        assertThat(users).anyMatch(user ->
                user.getName().equals("User One"));

        assertThat(users).anyMatch(user ->
                user.getBirthday().equals(LocalDate.now()));
    }

    @Test
    public void updateUser() {
        User testUserUpdate = new User();

        testUserUpdate.setId(2L);
        testUserUpdate.setEmail("user02@example.com");
        testUserUpdate.setLogin("user02");
        testUserUpdate.setName("User ZeroTwo");
        testUserUpdate.setBirthday(LocalDate.now());

        userRepository.update(testUserUpdate);

        Optional<User> userOptional = userRepository.findById(2);

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                );

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("email",
                                "user02@example.com")
                );

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("login",
                                "user02")
                );

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("name",
                                "User ZeroTwo")
                );

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                LocalDate.now())
                );
    }

    @Test
    public void addFriend() {
        // запрос друзей юзера 2
        List<User> friendsOfUserTwo = userRepository.findUserFriends(2L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds = List.of();
        assertThat(friendsOfUserTwo).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds);

        // добавим юзеру 2 двух друзей с id = 3 и 4
        userRepository.addFriend(2L, 3L);
        userRepository.addFriend(2L, 4L);

        // убеждаемся, что они появились
        List<User> twoFriends = userRepository.findUserFriends(2L);

        // ожидаем конкретный список пользователей
        List<Long> expectedIdsAfterAdd = Arrays.asList(3L, 4L);
        assertThat(twoFriends).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIdsAfterAdd);
    }

    @Test
    public void removeFriend() {
        // запрос друзей юзера 2
        List<User> friendsOfUserTwo = userRepository.findUserFriends(2L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds = List.of();
        assertThat(friendsOfUserTwo).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds);

        // добавим юзеру 2 двух друзей с id = 3 и 4
        userRepository.addFriend(2L, 3L);
        userRepository.addFriend(2L, 4L);

        // убеждаемся, что они появились
        List<User> twoFriends = userRepository.findUserFriends(2L);

        List<Long> expectedIds = List.of(3L, 4L);
        assertThat(twoFriends).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);

        //  удаляем юзера с id = 4
        List<User> oneFriend = userRepository.removeFriend(2L, 4L);

        //  убеждаемся, что остался только юзер с id = 3
        List<Long> expectedIdsAfterDel = List.of(3L);
        assertThat(oneFriend).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIdsAfterDel);
    }

    @Test
    public void findUserFriends() {
        // запрос друзей юзера 2
        List<User> friendsOfUserTwo = userRepository.findUserFriends(2L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds = List.of();
        assertThat(friendsOfUserTwo).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds);

        // добавим юзеру 2 двух друзей с id = 3 и 4
        userRepository.addFriend(2L, 3L);
        userRepository.addFriend(2L, 4L);

        // получаем список друзей юзера 2 через поиск
        List<User> findedFriends = userRepository.findUserFriends(2L);

        //  убеждаемся, что нашлись два друга с id = 3 и 4
        List<Long> expectedIds = List.of(3L, 4L);
        assertThat(findedFriends).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    public void findCommonFriends() {
        // запрос друзей юзера 2
        List<User> friendsOfUser2 = userRepository.findUserFriends(2L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds2 = List.of();
        assertThat(friendsOfUser2).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds2);

        // запрос друзей юзера 5
        List<User> friendsOfUser5 = userRepository.findUserFriends(5L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds5 = List.of();
        assertThat(friendsOfUser5).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds5);

        // добавим юзеру 2 двух друзей с id = 3 и 4
        userRepository.addFriend(2L, 3L);
        userRepository.addFriend(2L, 4L);

        // добавим юзеру 5 двух друзей с id = 4 и 6
        userRepository.addFriend(5L, 4L);
        userRepository.addFriend(5L, 6L);

        // получаем список общих друзей юзеров 2 и 5
        List<User> commonFriends = userRepository.findCommonFriends(2L, 5L);

        //  убеждаемся, что у юзеров 2 и 5 только один общих друг - юзер 4
        List<Long> expectedIds = List.of(4L);
        assertThat(commonFriends).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }
}