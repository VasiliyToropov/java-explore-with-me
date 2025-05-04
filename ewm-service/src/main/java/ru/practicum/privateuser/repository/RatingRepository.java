package ru.practicum.privateuser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.rating.Rating;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Rating findByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT e FROM Event e " +
            "WHERE (:rangeStart IS NULL OR e.likes >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR e.likes <= :rangeEnd) " +
            "ORDER BY e.likes DESC")
    List<Event> getEventsByLikes(@Param("rangeStart") Long rangeStart,
                                 @Param("rangeEnd") Long rangeEnd);

    @Query("SELECT e FROM Event e " +
            "WHERE (:rangeStart IS NULL OR e.dislikes >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR e.dislikes <= :rangeEnd) " +
            "ORDER BY e.dislikes DESC")
    List<Event> getEventsByDisLikes(@Param("rangeStart") Long rangeStart,
                                    @Param("rangeEnd") Long rangeEnd);

    @Query("SELECT r.userId FROM Rating r " +
            "GROUP BY r.userId " +
            "ORDER BY COUNT(r.userId) DESC")
    List<Long> getActiveUsers();
}
