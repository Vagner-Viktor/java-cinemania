package cinemania.controller;

import cinemania.model.Film;
import cinemania.service.FilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @GetMapping
    public Collection<Film> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return service.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return service.update(newFilm);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id,
                        @PathVariable Long userId,
                        @RequestParam Integer mark) {
        return service.addLike(id, userId, mark);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        return service.deleteLike(id, userId);
    }

    // пример запроса будет выглядить так GET /films/popular?count={limit}&genreId={genreId}&year={year}
    @GetMapping("/popular")
    public Collection<Film> getPopular(
            @RequestParam(defaultValue = "10", required = false) Long count,
            @RequestParam(defaultValue = "0", required = false) Long genreId,
            @RequestParam(defaultValue = "0", required = false) int year) {
        return service.getPopular(count, genreId, year);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(
            @RequestParam(defaultValue = "", required = false) String query,
            @RequestParam(defaultValue = "title", required = false) List<String> by) {
        return service.searchFilms(query, by);
    }

    @GetMapping("/director/{id}")
    public Collection<Film> getFilmsByDirector(@PathVariable Long id, @RequestParam String sortBy) {
        return service.getFilmsByDirector(id, sortBy);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Long userId,
                                           @RequestParam Long friendId) {
        return service.getCommonFilms(userId, friendId);
    }
}
