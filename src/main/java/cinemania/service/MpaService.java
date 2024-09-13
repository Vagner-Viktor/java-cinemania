package cinemania.service;

import cinemania.model.Mpa;
import cinemania.storage.MpaDbStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage storage;

    public Collection<Mpa> findAll() {
        return storage.findAll();
    }

    public Mpa findById(int id) {
        return storage.findById(id);
    }
}
