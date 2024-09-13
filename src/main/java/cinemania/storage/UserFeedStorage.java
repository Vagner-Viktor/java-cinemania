package cinemania.storage;

import cinemania.model.UserFeed;

import java.util.Collection;

public interface UserFeedStorage {
    UserFeed create(UserFeed userFeed);

    Collection<UserFeed> findUserFeeds(Long id);
}
