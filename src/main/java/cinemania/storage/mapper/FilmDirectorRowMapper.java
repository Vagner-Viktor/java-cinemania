package cinemania.storage.mapper;

import cinemania.model.FilmDirector;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmDirectorRowMapper implements RowMapper<FilmDirector> {
    @Override
    public FilmDirector mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmDirector(rs.getLong("film_id"),
                rs.getLong("director_id"),
                rs.getString("name"));
    }
}
