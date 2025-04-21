package ru.practicum.publicuser.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;
import ru.practicum.exceptions.exception.BadRequestException;
import ru.practicum.exceptions.exception.NotFoundException;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.EventShortDto;
import ru.practicum.publicuser.repository.PublicEventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final PublicEventRepository eventRepository;
    private final StatClient statClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Map<Long, Set<String>> uniqueIdsForEvents = new HashMap<>();

    @Override
    public List<EventShortDto> getEventsByPublicUser(String text,
                                                     List<Long> categories,
                                                     Boolean paid,
                                                     String rangeStart,
                                                     String rangeEnd,
                                                     Boolean onlyAvailable,
                                                     String sort,
                                                     Integer from,
                                                     Integer size,
                                                     HttpServletRequest request) {

        validateDateRange(rangeStart, rangeEnd);

        Pageable pageable = PageRequest.of(
                (from == null) ? 0 : from / ((size == null) ? 10 : size),
                (size == null) ? 10 : size
        );

        sendStatistic(request);
        log.info("Получили список событий с выборкой");

        List<EventShortDto> events = eventRepository.getEventsByPublicUser(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, pageable
        );

        // Добавляем просмотры для каждого события с уникальным ip
        String ip = request.getRemoteAddr();
        events.forEach(eventDto -> {
            Event event = eventRepository.findById(eventDto.getId())
                    .orElseThrow(() -> new NotFoundException("Event with id=" + eventDto.getId() + " not found"));
            addViewToEvent(event, ip);
            // Обновляем views в DTO
            eventDto.setViews(event.getViews());
        });

        return events;
    }

    @Override
    public Event getEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != Event.State.PUBLISHED) {
            throw new NotFoundException("Event with id=" + id + " was not published");
        }

        addViewToEvent(event, request.getRemoteAddr());
        sendStatistic(request);
        log.info("Получили событие с id: {}", id);

        return event;
    }

    private void validateDateRange(String rangeStart, String rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, FORMATTER);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, FORMATTER);
            if (start.isAfter(end)) {
                throw new BadRequestException("rangeStart не может быть позже rangeEnd");
            }
        }
    }

    private void sendStatistic(HttpServletRequest request) {

        String now = LocalDateTime.now().format(FORMATTER);

        EndpointHitDto endpointHitDto = new EndpointHitDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                now
        );
        statClient.postEndpointHit(endpointHitDto);
    }

    private void addViewToEvent(Event event, String ip) {
        Set<String> ipsForEvent = uniqueIdsForEvents.computeIfAbsent(event.getId(), k -> new HashSet<>());

        if (!ipsForEvent.contains(ip)) {
            event.setViews(event.getViews() + 1);
            ipsForEvent.add(ip);
            eventRepository.save(event);
        }
    }

    // Перегруженный метод для работы с IP напрямую
    private void addViewToEvent(Event event, HttpServletRequest request) {
        addViewToEvent(event, request.getRemoteAddr());
    }
}