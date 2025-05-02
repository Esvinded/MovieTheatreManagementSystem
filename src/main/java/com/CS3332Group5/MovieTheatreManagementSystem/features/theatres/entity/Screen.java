package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import com.CS3332Group5.MovieTheatreManagementSystem.features.showtimes.entity.Showtime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.util.Set;

@Entity @Table(name="screens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Screen extends BaseEntity {
    @NotBlank private String name;
    @Positive private Integer capacity;
    @Enumerated(EnumType.STRING) private Status status = Status.ACTIVE;
    @ManyToOne @JoinColumn(name="theatre_id") private Theatre theatre;
    @OneToMany(mappedBy="screen") private Set<Showtime> showtimes;
}