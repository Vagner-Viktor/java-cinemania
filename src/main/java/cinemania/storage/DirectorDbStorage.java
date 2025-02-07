package cinemania.storage;

import cinemania.exception.NotFoundException;
import cinemania.exception.ValidationException;
import cinemania.model.Director;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Slf4j
@Primary
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String DIRECTORS_FIND_ALL_QUERY = """
            SELECT *
            FROM "directors"
            """;
    private static final String DIRECTOR_FIND_BY_ID_QUERY = """
            SELECT *
            FROM "directors"
            WHERE "director_id" = ?
            """;
    private static final String DIRECTORS_ADD_LIKE_QUERY = """
            INSERT INTO "directors" ("name")
            VALUES (?);
            """;
    private static final String DIRECTORS_UPDATE_LIKE_QUERY = """
            UPDATE "directors"
            SET "name" = ?
            WHERE "director_id" = ?;
            """;
    private static final String DIRECTORS_DELETE_QUERY = """
            DELETE FROM "directors"
            WHERE "director_id" = ?
            """;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Director> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<Director> getAllDirectors() {
        log.info("Получение списка режиссеров");
        return findMany(DIRECTORS_FIND_ALL_QUERY);
    }

    @Override
    public Director getDirectorById(Long id) {
        if (id == null) {
            throw new NotFoundException("Id режисера должен быть указан");
        }
        log.info("Получение режиссера по id = {}", id);
        return findOne(DIRECTOR_FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + id + " не найден!"));
    }

    @Override
    public Director addDirector(Director director) {
        Long id = insertGetKey(DIRECTORS_ADD_LIKE_QUERY, director.getName());
        director.setId(id);
        log.info("Режиссер {} добавлен в список с id = {}", director.getName(), director.getId());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (director.getId() == null) {
            throw new ValidationException("Id режисера должен быть указан");
        }
        if (isDirectorExists(director.getId())) {
            update(DIRECTORS_UPDATE_LIKE_QUERY, director.getName(), director.getId());
            log.info("Режиссер с id = {} обновлен", director.getId());
            return director;
        } else {
            throw new NotFoundException("Режиссер с id = " + director.getId() + " не найден");
        }
    }

    @Override
    public Long deleteDirector(Long id) {
        if (isDirectorExists(id)) {
            delete(DIRECTORS_DELETE_QUERY, id);
            log.info("Режиссер с id = {} удален", id);
            return id;
        } else {
            throw new NotFoundException("Режиссер с id = " + id + " не найден");
        }
    }

    @Override
    public boolean isDirectorExists(Long id) {
        return findOne(
                DIRECTOR_FIND_BY_ID_QUERY,
                id).isPresent();
    }
}
