package ru.practicum.publicuser.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.EventShortDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEventsByPublicUser(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            HttpServletRequest request);

    public Event getEventById(Long id, HttpServletRequest request);
}
