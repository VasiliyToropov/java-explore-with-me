package ru.practicum.privateuser.model.location;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "locations")
public class LocationDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    private Float lat;

    private Float lon;
}
