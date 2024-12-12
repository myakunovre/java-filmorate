MERGE INTO PUBLIC.USERS (ID, EMAIL, LOGIN, NAME, BIRTHDAY) KEY(ID)
VALUES
(2, 'user2@example.com', 'user2', 'User Two', DATE '1990-01-01'),
(3, 'user3@example.com', 'user3', 'User Three', DATE '1992-02-02'),
(4, 'user4@example.com', 'user4', 'User Four', DATE '1995-03-03'),
(5, 'user5@example.com', 'user5', 'User Five', DATE '1998-04-04'),
(6, 'user6@example.com', 'user6', 'User Six', DATE '2000-05-05');

MERGE INTO PUBLIC.FRIENDS USING DUAL
ON (USER_ID = 3 AND FRIEND_ID = 4)
WHEN NOT MATCHED THEN
INSERT (USER_ID, FRIEND_ID) VALUES (3, 4);

MERGE INTO PUBLIC.RATINGS (ID, NAME) KEY(ID)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

MERGE INTO PUBLIC.FILMS (ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) KEY(ID)
VALUES
(2, 'Film B', 'Description for Film B', '2022-01-02', 90, 2),
(3, 'Film C', 'Description for Film C', '2022-01-03', 150, 2),
(4, 'Film D', 'Description for Film D', '2022-01-04', 120, 2);

MERGE INTO PUBLIC.GENRES (ID, NAME) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

--INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES
--(2, 1),
--(3, 2),
--(4, 3)
--ON CONFLICT (FILM_ID, GENRE_ID) DO NOTHING;
INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID)
SELECT * FROM (
    SELECT 2 AS FILM_ID, 1 AS GENRE_ID FROM DUAL
    UNION ALL
    SELECT 3, 2 FROM DUAL
    UNION ALL
    SELECT 4, 3 FROM DUAL
) AS new_values
WHERE NOT EXISTS (
    SELECT 1 FROM PUBLIC.FILM_GENRE fg
    WHERE fg.FILM_ID = new_values.FILM_ID AND fg.GENRE_ID = new_values.GENRE_ID
);

--INSERT INTO PUBLIC.LIKES (FILM_ID, USER_ID) VALUES
--(3, 3),
--(4, 4);