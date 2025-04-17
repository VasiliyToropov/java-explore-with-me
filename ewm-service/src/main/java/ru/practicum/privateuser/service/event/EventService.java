package ru.practicum.privateuser.service.event;

import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.NewEventDto;
import ru.practicum.privateuser.model.request.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    public List<Event> getEventsByUser(Long userId, Integer from, Integer size);

    public Event addNewEvent(Long userId, NewEventDto newEventDto);

    public Event getEventByEventId(Long eventId);

    public Event updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

}
