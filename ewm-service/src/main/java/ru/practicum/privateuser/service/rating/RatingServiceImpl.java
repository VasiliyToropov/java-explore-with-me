package ru.practicum.privateuser.service.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.user.User;
import ru.practicum.admin.service.user.UserServiceImpl;
import ru.practicum.exceptions.exception.ForbiddenException;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.rating.Rating;
import ru.practicum.privateuser.model.rating.RatingDto;
import ru.practicum.privateuser.repository.EventRepository;
import ru.practicum.privateuser.repository.RatingRepository;
import ru.practicum.privateuser.service.event.EventServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final EventServiceImpl eventService;
    private final EventRepository eventRepository;
    private final UserServiceImpl userService;

    @Override
    public List<Event> getEventsByLikes(Long userId, Long rangeStart, Long rangeEnd) {
        return ratingRepository.getEventsByLikes(rangeStart, rangeEnd);
    }

    @Override
    public List<Event> getEventsByDislikes(Long userId, Long rangeStart, Long rangeEnd) {
        return ratingRepository.getEventsByDisLikes(rangeStart, rangeEnd);
    }

    @Override
    public Event addRatingToEvent(Long userId, Long eventId, RatingDto ratingDto) {

        Event event = eventService.getEventByEventId(eventId);

        //Событие должно быть опубликовано
        if (!event.getState().equals(Event.State.PUBLISHED)) {
            throw new ForbiddenException("Оценку можно добавить только к опубликованному событию");
        }

        //Нельзя ставить оценку повторно к одному событию от одного пользователя
        Rating foundedRating = ratingRepository.findByUserIdAndEventId(userId, eventId);
        if (foundedRating != null) {
            throw new ForbiddenException("Пользователь уже поставил оценку к этому событию");
        }

        Rating rating = new Rating();
        rating.setUserId(ratingDto.getUserId());
        rating.setEventId(ratingDto.getEventId());
        rating.setType(Rating.RatingType.valueOf(ratingDto.getRatingType()));

        ratingRepository.save(rating);

        if (ratingDto.getRatingType().equals("LIKE")) {
            event.setLikes(event.getLikes() + 1);
        } else {
            event.setDislikes(event.getDislikes() + 1);
        }

        eventRepository.save(event);

        return event;
    }


    @Override
    public List<User> getMostActiveUsers() {

        List<User> activeUsers = new ArrayList<>();
        List<Long> usersIds = ratingRepository.getActiveUsers();

        for (Long id : usersIds) {
            activeUsers.add(userService.getUserById(id));
        }

        return activeUsers;
    }
}
