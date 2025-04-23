package ru.practicum.privateuser.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.rating.RatingDto;
import ru.practicum.privateuser.service.rating.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    //Получение событий с фильтрацией по количеству лайков или диапазоном рейтинга
    @GetMapping("/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getEventsByLikes(@PathVariable Long userId,
                                        @RequestParam(required = false) Long rangeStart,
                                        @RequestParam(required = false) Long rangeEnd) {
        return ratingService.getEventsByLikes(userId, rangeStart, rangeEnd);
    }

    //Получение событий с фильтрацией по количеству дизлайков или диапазоном рейтинга
    @GetMapping("/dislikes")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getEventsByDislikes(@PathVariable Long userId,
                                           @RequestParam(required = false) Long rangeStart,
                                           @RequestParam(required = false) Long rangeEnd) {
        return ratingService.getEventsByDislikes(userId, rangeStart, rangeEnd);
    }

    //Добавление рейтинга к событию
    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Event addRatingToEvent(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @RequestBody @Valid RatingDto ratingDto) {
        return ratingService.addRatingToEvent(userId, eventId, ratingDto);
    }
}
