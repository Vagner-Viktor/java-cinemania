package cinemania.storage;

import cinemania.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Collection<Director> getAllDirectors();

    Director getDirectorById(Long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Long deleteDirector(Long id);

    boolean isDirectorExists(Long id);
}
