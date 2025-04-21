package ru.practicum.privateuser.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.EventRequestStatusUpdateRequest;
import ru.practicum.privateuser.model.event.EventRequestStatusUpdateResult;
import ru.practicum.privateuser.model.event.NewEventDto;
import ru.practicum.privateuser.model.request.ParticipationRequest;
import ru.practicum.privateuser.model.request.UpdateEventUserRequest;
import ru.practicum.privateuser.service.event.EventService;
import ru.practicum.privateuser.service.request.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getEventsByUser(@PathVariable Long userId, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event addNewEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addNewEvent(userId, newEventDto);
    }


    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Event getEventByEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByEventId(eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Event updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                             @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequest> getRequestsByUserAndEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestsByUserAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeEventRequestStatus(@PathVariable Long userId,
                                                                   @PathVariable Long eventId,
                                                                   @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.changeEventRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
