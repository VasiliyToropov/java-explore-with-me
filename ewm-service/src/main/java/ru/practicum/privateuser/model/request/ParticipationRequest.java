package ru.practicum.privateuser.model.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    private String created;

    private Long event;

    private Long requester;

    @Enumerated
    @JoinColumn(name = "status")
    private Status status;

    public enum Status {
        PENDING, REJECTED, CONFIRMED, CANCELED
    }
}
