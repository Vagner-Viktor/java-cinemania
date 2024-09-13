package cinemania.storage;

import cinemania.model.FilmLike;

import java.util.Collection;

public interface FilmLikeStorage {
    Collection<FilmLike> findLikesOfFilms(String filmsId);
}
