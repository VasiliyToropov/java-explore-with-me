package ru.practicum.publicuser.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.EventShortDto;
import ru.practicum.publicuser.service.PublicEventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final PublicEventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByPublicUser(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            HttpServletRequest request) {

        return eventService.getEventsByPublicUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEventById(id, request);
    }
}
