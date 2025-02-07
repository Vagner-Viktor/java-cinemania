package cinemania.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private static final int DESCRIPTION_MAX_SIZE = 200;
    private Long id;
    @NotBlank
    private String name;

    @Size(max = DESCRIPTION_MAX_SIZE)
    private String description;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotNull
    private Duration duration;

    @Builder.Default
    private Collection<FilmLike> likes = new HashSet<>();

    @NotNull
    private Mpa mpa;

    @Builder.Default
    private Collection<Genre> genres = new HashSet<>();

    @Builder.Default
    private Collection<Director> directors = new HashSet<>();

    @JsonProperty("duration")
    @Positive
    public long getDurationTimeSeconds() {
        return duration.getSeconds();
    }

    public void addLike(FilmLike like) {
        likes.add(like);
    }

    public void deleteLike(FilmLike like) {
        likes.remove(like);
    }

    public int getLikesCount() {
        return likes.size();
    }
}

