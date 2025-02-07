package cinemania.storage;

import cinemania.model.FilmGenre;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
@Primary
public class FilmGenreDBStorage extends BaseDbStorage<FilmGenre> implements FilmGenreStorage {
    private static final String GENRES_FIND_BY_FILM_ID_QUERY = """
            SELECT fg."film_id", g."genre_id", g."genre"
            FROM "films_genre" AS fg
            JOIN "genres" AS g ON fg."genre_id" = g."genre_id"
            WHERE "film_id" IN (%s)
            ORDER BY fg."film_id", g."genre_id";
            """;

    public FilmGenreDBStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<FilmGenre> findGenresOfFilms(String filmsId) {
        return findMany(
                String.format(GENRES_FIND_BY_FILM_ID_QUERY, filmsId)
        );
    }
}
