package com.CS3332Group5.MovieTheatreManagementSystem.features.theatres.entity;

import com.CS3332Group5.MovieTheatreManagementSystem.common.BaseEntity;
import com.CS3332Group5.MovieTheatreManagementSystem.common.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Set;

@Entity @Table(name="theatres")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Theatre extends BaseEntity {
    @NotBlank private String name;
    private String address;
    @Enumerated(EnumType.STRING) private Status status = Status.ACTIVE;
    @OneToMany(mappedBy="theatre", cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<Screen> screens;
}