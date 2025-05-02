package com.CS3332Group5.MovieTheatreManagementSystem.features.movies.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Duration;
import java.util.Set;

@Entity @Table(name="movies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Movie extends BaseEntity {
    @NotBlank private String title;
    private String description;
    @NotNull private Duration duration;
    @DecimalMin("0.0") @DecimalMax("10.0") private Double rating;
    @Enumerated(EnumType.STRING) private Status status = Status.ACTIVE;

    @OneToMany(mappedBy="movie") private Set<Showtime> showtimes;
}