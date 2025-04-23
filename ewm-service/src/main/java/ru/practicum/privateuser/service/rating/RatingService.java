package ru.practicum.privateuser.service.rating;

import ru.practicum.admin.model.user.User;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.rating.RatingDto;

import java.util.List;

public interface RatingService {

    List<Event> getEventsByLikes(Long userId, Long rangeStart, Long rangeEnd);

    List<Event> getEventsByDislikes(Long userId, Long rangeStart, Long rangeEnd);

    Event addRatingToEvent(Long userId, Long eventId, RatingDto ratingDto);

    List<User> getMostActiveUsers();
}
