package ru.practicum.privateuser.model.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.admin.model.category.Category;
import ru.practicum.admin.model.user.User;
import ru.practicum.privateuser.model.location.LocationDb;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(name = "annotation", columnDefinition = "text")
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "confirmed_request")
    private int confirmedRequests;

    @Column(name = "created_on")
    private String createdOn;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "event_date")
    private String eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationDb location;

    private boolean paid;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "published_on")
    private String publishedOn;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    private String title;

    private int views;

    private Long likes;

    private Long dislikes;

    public enum State {
        PENDING, PUBLISHED, CANCELED
    }
}
