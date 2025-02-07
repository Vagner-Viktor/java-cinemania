package cinemania.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDirector {
    private Long filmId;
    private Long directorId;
    private String name;
}
