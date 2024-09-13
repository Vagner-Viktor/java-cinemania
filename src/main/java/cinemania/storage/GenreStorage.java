package cinemania.storage;

import cinemania.model.Genre;

import java.util.Collection;

public interface GenreStorage {
    Collection<Genre> findAll();

    Genre findById(int id);

    void checkGenresExists(Collection<Genre> genres);

    boolean isGenreExists(int id);
}
