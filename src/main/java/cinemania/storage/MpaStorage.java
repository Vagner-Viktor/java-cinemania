package cinemania.storage;

import cinemania.model.Mpa;

import java.util.Collection;

public interface MpaStorage {
    Collection<Mpa> findAll();

    Mpa findById(int id);

    boolean isMpaExists(int id);
}
