package cinemania.storage.mapper;

import cinemania.model.Friend;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendRowMapper implements RowMapper<Friend> {
    @Override
    public Friend mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Friend(
                rs.getLong("friend_id"),
                rs.getInt("friendship_status_id")
        );
    }
}
