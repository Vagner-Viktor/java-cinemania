package cinemania.storage;

import cinemania.model.Friend;

import java.util.Collection;

public interface FriendStorage {
    Collection<Friend> findFriendsOfUser(Long id);
}
