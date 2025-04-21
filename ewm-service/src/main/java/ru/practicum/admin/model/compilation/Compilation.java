package ru.practicum.admin.model.compilation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.privateuser.model.event.Event;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events = new ArrayList<>();

    private Boolean pinned;

    private String title;
}
