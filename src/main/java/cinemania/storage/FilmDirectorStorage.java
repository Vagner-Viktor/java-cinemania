package cinemania.storage;

import cinemania.model.FilmDirector;

import java.util.Collection;

public interface FilmDirectorStorage {
    Collection<FilmDirector> findDirectorsOfFilms(String filmsId);
}
