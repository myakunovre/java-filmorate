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
        assertThat(users).hasSize(6);

        // Или если ожидаете конкретный список пользователей
        List<Long> expectedIds = Arrays.asList(10001L, 10002L, 10003L, 10004L, 10005L, 10006L);
        assertThat(users).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional1 = userRepository.findById(10001L);

        assertThat(userOptional1)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 10001L)
                );

        Optional<User> userOptional2 = userRepository.findById(10002L);

        assertThat(userOptional2)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 10002L)
                );
    }

    @Test
    public void findUserByEmail() {
        Optional<User> userOptional = userRepository.findByEmail("user10001@example.com");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 10001L)
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

        testUserUpdate.setId(10001L);
        testUserUpdate.setEmail("user10001update@example.com");
        testUserUpdate.setLogin("user1update");
        testUserUpdate.setName("User One Update");
        testUserUpdate.setBirthday(LocalDate.now());

        userRepository.update(testUserUpdate);

        Optional<User> userOptional = userRepository.findById(10001L);

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("id", 10001L)
                );

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("email",
                                "user10001update@example.com")
                );

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("login",
                                "user1update")
                );

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("name",
                                "User One Update")
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
        List<User> friendsOfUserTwo = userRepository.findUserFriends(10002L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds = List.of();
        assertThat(friendsOfUserTwo).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds);

        // добавим юзеру 2 двух друзей с id = 3 и 4
        userRepository.addFriend(10002L, 10003L);
        userRepository.addFriend(10002L, 10004L);

        // убеждаемся, что они появились
        List<User> twoFriends = userRepository.findUserFriends(10002L);

        // ожидаем конкретный список пользователей
        List<Long> expectedIdsAfterAdd = Arrays.asList(10003L, 10004L);
        assertThat(twoFriends).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIdsAfterAdd);
    }

    @Test
    public void removeFriend() {
        // запрос друзей юзера 2
        List<User> friendsOfUserTwo = userRepository.findUserFriends(10002L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds = List.of();
        assertThat(friendsOfUserTwo).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds);

        // добавим юзеру 2 двух друзей с id = 3 и 4
        userRepository.addFriend(10002L, 10003L);
        userRepository.addFriend(10002L, 10004L);

        // убеждаемся, что они появились
        List<User> twoFriends = userRepository.findUserFriends(10002L);

        // ожидаем конкретный список пользователей
        List<Long> expectedIdsAfterAdd = Arrays.asList(10003L, 10004L);
        assertThat(twoFriends).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIdsAfterAdd);

        //  удаляем юзера с id = 10004L
        List<User> oneFriend = userRepository.removeFriend(10002L, 10004L);

        //  убеждаемся, что остался только юзер с id = 3
        List<Long> expectedIdsAfterDel = List.of(10003L);
        assertThat(oneFriend).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIdsAfterDel);
    }

    @Test
    public void findUserFriends() {
        // запрос друзей юзера 2
        List<User> friendsOfUserTwo = userRepository.findUserFriends(10002L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds = List.of();
        assertThat(friendsOfUserTwo).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds);

        // добавим юзеру 2 двух друзей с id = 3 и 4
        userRepository.addFriend(10002L, 10003L);
        userRepository.addFriend(10002L, 10004L);

        // получаем список друзей юзера 2 через поиск
        List<User> findedFriends = userRepository.findUserFriends(10002L);

        //  убеждаемся, что нашлись два друга с id = 3 и 4
        List<Long> expectedIds = List.of(10003L, 10004L);
        assertThat(findedFriends).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    public void findCommonFriends() {
        // запрос друзей юзера 2
        List<User> friendsOfUser2 = userRepository.findUserFriends(10002L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds2 = List.of();
        assertThat(friendsOfUser2).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds2);

        // запрос друзей юзера 5
        List<User> friendsOfUser5 = userRepository.findUserFriends(10005L);

        // убеждаемся, что их нет
        List<Long> expectedNoIds5 = List.of();
        assertThat(friendsOfUser5).extracting("id").containsExactlyInAnyOrderElementsOf(expectedNoIds5);

        // добавим юзеру 2 двух друзей с id = 3 и 4
        userRepository.addFriend(10002L, 10003L);
        userRepository.addFriend(10002L, 10004L);

        // добавим юзеру 5 двух друзей с id = 4 и 6
        userRepository.addFriend(10005L, 10004L);
        userRepository.addFriend(10005L, 10006L);

        // получаем список общих друзей юзеров 2 и 5
        List<User> commonFriends = userRepository.findCommonFriends(10002L, 10005L);

        //  убеждаемся, что у юзеров 2 и 5 только один общих друг - юзер 4
        List<Long> expectedIds = List.of(10004L);
        assertThat(commonFriends).extracting("id").containsExactlyInAnyOrderElementsOf(expectedIds);
    }
}