package cinemania.storage;

import cinemania.model.FilmGenre;

import java.util.Collection;

public interface FilmGenreStorage {
    Collection<FilmGenre> findGenresOfFilms(String filmsId);
}
