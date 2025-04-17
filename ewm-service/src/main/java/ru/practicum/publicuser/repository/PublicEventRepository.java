package ru.practicum.publicuser.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.EventShortDto;

import java.util.List;

@Repository
public interface PublicEventRepository extends JpaRepository<Event, Long> {
    @Query("""
                SELECT new ru.practicum.privateuser.model.event.EventShortDto(
                    e.id, e.annotation, e.category, e.confirmedRequests, e.description,
                    e.eventDate, e.initiator, e.paid, e.title, e.views)
                FROM Event e
                WHERE e.state = 'PUBLISHED'
                AND (:text IS NULL
                            OR LOWER(CAST(e.annotation AS string)) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%'))
                            OR LOWER(CAST(e.description AS string)) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%')))
                AND (:categories IS NULL OR e.category.id IN :categories)
                AND (:paid IS NULL OR e.paid = :paid)
                AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
                AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
                AND (:onlyAvailable IS NULL OR :onlyAvailable = FALSE
                    OR e.participantLimit = 0
                    OR e.confirmedRequests < e.participantLimit)
                ORDER BY
                    CASE WHEN :sort IS NULL THEN e.id END ASC,
                    CASE WHEN :sort = 'EVENT_DATE' THEN e.eventDate END ASC,
                    CASE WHEN :sort = 'VIEWS' THEN e.views END DESC
            """)
    List<EventShortDto> getEventsByPublicUser(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") String rangeStart,
            @Param("rangeEnd") String rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            @Param("sort") String sort,
            Pageable pageable);

}
