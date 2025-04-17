package ru.practicum.privateuser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.privateuser.model.request.ParticipationRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequester(Long requester);

    @Query("SELECT r FROM ParticipationRequest r WHERE r.event = ?1")
    List<ParticipationRequest> findAllByEventId(Long eventId);
}
