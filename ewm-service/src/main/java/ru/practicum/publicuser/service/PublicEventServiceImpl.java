package ru.practicum.publicuser.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final PublicEventRepository eventRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Set<String> uniqueIps = new HashSet<>();

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


        //rangeStart не может быть позже rangeEnd
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, FORMATTER);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, FORMATTER);
            if (start.isAfter(end)) {
                throw new BadRequestException("rangeStart не может быть позже rangeEnd");
            }
        }

        // Если from и size не заданы, то устанавливаем значения по умолчанию
        int defaultFrom = (from == null) ? 0 : from;
        int defaultSize = (size == null) ? 10 : size;

        Pageable pageable = PageRequest.of(defaultFrom / defaultSize, defaultSize);

        //Отправляем статистику
        sendStatistic(request);

        log.info("Получили список событий с выборкой");

        return eventRepository.getEventsByPublicUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, pageable);
    }

    @Override
    public Event getEventById(Long id, HttpServletRequest request) {

        Optional<Event> foundedEvent = eventRepository.findById(id);

        Event event = foundedEvent.orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        //Если событие недоступно
        if (event.getState() != Event.State.PUBLISHED) {
            throw new NotFoundException("Event with id=" + id + " was not published");
        }

        //Добавляем просмотр для уникального IP
        String ip = request.getRemoteAddr();

        if (!uniqueIps.contains(ip)) {
            int views = event.getViews() + 1;
            event.setViews(views);
            uniqueIps.add(ip);
            eventRepository.save(event);
        }


        //Отправляем статистику
        sendStatistic(request);

        log.info("Получили событие с id: {}", id);

        return event;
    }

    //Формируем статистику и отправляем в клиент
    public void sendStatistic(HttpServletRequest request) {
        String app = "ewm-main-service";
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        LocalDateTime now = LocalDateTime.now();

        EndpointHitDto endpointHitDto = new EndpointHitDto(app, uri, ip, now);

        RestTemplateBuilder builder = new RestTemplateBuilder();

        StatClient statClient = new StatClient(builder);

        statClient.postEndpointHit(endpointHitDto);
    }
}
