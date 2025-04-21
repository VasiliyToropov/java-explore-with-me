package ru.practicum.privateuser.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.privateuser.model.event.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.initiator.id = ?1 ORDER BY e.createdOn ASC")
    List<Event> getEventsByUser(Long userId, Pageable pageable);
}
