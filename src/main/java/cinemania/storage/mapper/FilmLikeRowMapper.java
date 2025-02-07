package cinemania.storage.mapper;

import cinemania.model.FilmLike;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmLikeRowMapper implements RowMapper<FilmLike> {
    @Override
    public FilmLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmLike(
                rs.getLong("film_id"),
                rs.getLong("user_id"),
                rs.getInt("mark"));
    }
}
