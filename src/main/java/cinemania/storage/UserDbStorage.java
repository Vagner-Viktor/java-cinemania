package cinemania.storage;

import cinemania.exception.DuplicatedDataException;
import cinemania.exception.NotFoundException;
import cinemania.exception.ValidationException;
import cinemania.model.Friend;
import cinemania.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final int USERS_FRIENDSHIP_STATUS_CONFIRMED = 1;
    private static final int USERS_FRIENDSHIP_STATUS_UNCONFIRMED = 2;
    private static final String USERS_FIND_ALL_QUERY = """
            SELECT *
            FROM "users";
            """;
    private static final String USERS_INSERT_QUERY = """
            INSERT INTO "users" ("email", "login", "username", "birthday")
                        VALUES (?, ?, ?, ?);
            """;
    private static final String USERS_UPDATE_QUERY = """
            UPDATE "users"
            SET "email" = ?,
                "login" = ?,
                "username" = ?,
                "birthday" = ?
            WHERE "user_id" = ?;
            """;
    private static final String USERS_ADD_TO_FRIENDS_QUERY = """
            INSERT INTO "friends" ("user_id", "friend_id", "friendship_status_id")
            VALUES (?, ?, ?);
            """;
    private static final String USERS_DELETE_FROM_FRIENDS_QUERY = """
            DELETE FROM "friends"
            WHERE "user_id" = ?
                AND "friend_id" = ?;
            """;
    private static final String USERS_FIND_ALL_FRIENDS_QUERY = """
            SELECT *
            FROM "users" AS u
            WHERE "user_id" IN (
                SELECT "friend_id"
                FROM "friends"
                WHERE "user_id" = ?
                );
            """;
    private static final String USERS_FIND_COMMON_FRIENDS_QUERY = """
            SELECT *
            FROM "users" AS u
            WHERE u."user_id" IN (
                SELECT friends_of_first.friend
                FROM (
                    SELECT "friend_id" AS friend FROM "friends" WHERE "user_id" = ?
                    UNION
                    SELECT "user_id" AS friend FROM "friends" WHERE "friend_id" = ?
                    ) AS friends_of_first
                JOIN (
                    SELECT "friend_id" AS friend FROM "friends" WHERE "user_id" = ?
                    UNION
                    SELECT "user_id" AS friend FROM "friends" WHERE "friend_id" = ?
                    ) AS friends_of_second
                ON friends_of_first.friend = friends_of_second.friend
            );
            """;
    private static final String USERS_FIND_BY_ID_QUERY = """
            SELECT *
            FROM "users"
            WHERE "user_id" = ?;
            """;
    private static final String USERS_FIND_BY_EMAIL_QUERY = """
            SELECT *
            FROM "users"
            WHERE "email" = ?;
            """;
    private static final String USERS_DELETE = """
            DELETE FROM "users"
            WHERE "user_id" = ?;
            """;

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получение списка пользователей");
        return findMany(USERS_FIND_ALL_QUERY);
    }

    @Override
    public User findById(Long id) {
        List<User> users = findMany(
                USERS_FIND_BY_ID_QUERY,
                id
        );
        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.getFirst();

    }

    @Override
    public User create(User user) {
        validate(user);
        long id = insertGetKey(
                USERS_INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        log.info("Пользователь {} добавлен в список с id = {}", user.getName(), user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new NotFoundException("Id пользователя должен быть указан");
        }
        if (isUserExists(user.getId())) {
            validate(user);
            update(
                    USERS_UPDATE_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    java.sql.Date.valueOf(user.getBirthday()),
                    user.getId()
            );
            log.info("Пользователь с id = {} обновлен", user.getId());
            return user;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    // удаление юзера по id, модифицировал связи в schema,  при удалении юзераа удаляются зависимые записи по id
    @Override
    public void delete(Long id) {
        if (!isUserExists(id))
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        delete(USERS_DELETE, id);
        log.info("Пользователь с id = {} удален", id);
    }

    @Override
    public User addToFriends(Long id, Long friendId) {
        User user = findOne(
                USERS_FIND_BY_ID_QUERY,
                id
        ).orElse(null);
        insert(
                USERS_ADD_TO_FRIENDS_QUERY,
                id,
                friendId,
                USERS_FRIENDSHIP_STATUS_UNCONFIRMED
        );
        user.addFriend(new Friend(friendId, USERS_FRIENDSHIP_STATUS_UNCONFIRMED));
        log.info("Пользователь с id = {} и пользователь с id = {} теперь друзья", friendId, id);
        return user;
    }

    @Override
    public User deleteFromFriends(Long id, Long friendId) {
        delete(
                USERS_DELETE_FROM_FRIENDS_QUERY,
                id,
                friendId
        );
        log.info("Пользователь с id = {} и пользователь с id = {} больше не друзья", friendId, id);
        return null;
    }

    @Override
    public Collection<User> findAllFriends(Long id) {
        if (!isUserExists(id))
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        log.info("Поиск друзей пользователя с id = {}", id);
        return findMany(
                USERS_FIND_ALL_FRIENDS_QUERY,
                id
        );
    }

    @Override
    public Collection<User> findCommonFriends(Long id, Long otherId) {
        if (!isUserExists(id))
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        if (!isUserExists(otherId))
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        log.info("Поиск общих друзей пользователя с id = {} и пользователя с id = {}", id, otherId);
        return findMany(
                USERS_FIND_COMMON_FRIENDS_QUERY,
                id,
                id,
                otherId,
                otherId
        );
    }

    @Override
    public boolean isUserExists(Long id) {
        return findOne(
                USERS_FIND_BY_ID_QUERY,
                id).isPresent();
    }

    private void validate(User user) {
        if (isDuplicatedEmail(user.getEmail())) {
            throw new DuplicatedDataException("Этот e-mail уже используется");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелов");
        }
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }

    private boolean isDuplicatedEmail(String email) {
        return findOne(
                USERS_FIND_BY_EMAIL_QUERY,
                email).isPresent();
    }
}
