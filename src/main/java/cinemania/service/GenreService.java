package cinemania.service;

import cinemania.model.Genre;
import cinemania.storage.GenreStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage storage;

    public Collection<Genre> findAll() {
        return storage.findAll();
    }

    public Genre findById(int id) {
        return storage.findById(id);
    }
}
