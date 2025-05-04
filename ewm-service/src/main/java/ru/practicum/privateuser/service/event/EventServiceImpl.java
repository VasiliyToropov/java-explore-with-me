package ru.practicum.privateuser.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.category.Category;
import ru.practicum.admin.model.user.User;
import ru.practicum.admin.service.category.CategoryServiceImpl;
import ru.practicum.admin.service.user.UserServiceImpl;
import ru.practicum.exceptions.exception.BadRequestException;
import ru.practicum.exceptions.exception.ForbiddenException;
import ru.practicum.exceptions.exception.NotFoundException;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.NewEventDto;
import ru.practicum.privateuser.model.location.LocationDb;
import ru.practicum.privateuser.model.request.UpdateEventUserRequest;
import ru.practicum.privateuser.repository.EventRepository;
import ru.practicum.privateuser.service.location.LocationServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;
    private final LocationServiceImpl locationService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Event> getEventsByUser(Long userId, Integer from, Integer size) {

        // Если from и size не заданы, то устанавливаем значения по умолчанию
        int defaultFrom = (from == null) ? 0 : from;
        int defaultSize = (size == null) ? 10 : size;

        Pageable pageable = PageRequest.of(defaultFrom / defaultSize, defaultSize);

        log.info("Получили список событий для пользователя с id: {}", userId);

        return eventRepository.getEventsByUser(userId, pageable);
    }

    @Override
    public Event addNewEvent(Long userId, NewEventDto newEventDto) {

        //Дата и время на которые намечено событие, не может быть раньше, чем через два часа от текущего момента
        LocalDateTime afterTwoHoursFromNow = LocalDateTime.now().plusHours(2);
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER);
        if (eventDate.isBefore(afterTwoHoursFromNow)) {
            throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + eventDate);
        }

        //Находим категорию из БД
        Long catId = newEventDto.getCategory();
        Category category = categoryService.getCategoryById(catId);

        User user = userService.getUserById(userId);

        //Сохраняем локацию в БД
        LocationDb locationDb = locationService.addLocation(newEventDto.getLocation());

        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now().format(FORMATTER));
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(eventDate.format(FORMATTER));
        event.setInitiator(user);
        event.setLocation(locationDb);
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        //Пока событие еще не опубликовано
        event.setPublishedOn(null);
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setState(Event.State.PENDING);
        event.setTitle(newEventDto.getTitle());
        event.setViews(0);
        event.setLikes(0L);
        event.setDislikes(0L);

        log.info("Добавили новое событие с id: {}", event.getId());

        return eventRepository.save(event);
    }

    @Override
    public Event getEventByEventId(Long eventId) {
        Optional<Event> foundedEvent = eventRepository.findById(eventId);

        log.info("Получили событие с id: {}", eventId);

        return foundedEvent.orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public Event updateEvent(Long userId, Long eventId, UpdateEventUserRequest newEvent) {

        Event event = getEventByEventId(eventId);

        //Дата и время на которые намечено событие, не может быть раньше, чем через два часа от текущего момента
        if (newEvent.getEventDate() != null) {
            LocalDateTime afterTwoHoursFromNow = LocalDateTime.now().plusHours(2);
            LocalDateTime newEventDate = LocalDateTime.parse(newEvent.getEventDate(), FORMATTER);
            if (newEventDate.isBefore(afterTwoHoursFromNow)) {
                throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                        "Value: " + newEventDate);
            }

            //Нельзя изменить дату события на уже наступившую
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("You can't change events that have already happened");
            }
        }

        //Изменить можно только отмененные события или события в состоянии ожидания модерации
        if (event.getState() == Event.State.PUBLISHED) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }

        Optional.ofNullable(newEvent.getAnnotation()).ifPresent(event::setAnnotation);

        if (newEvent.getCategory() != null) {
            Category category = categoryService.getCategoryById(newEvent.getCategory());
            event.setCategory(category);
        }

        Optional.ofNullable(newEvent.getDescription()).ifPresent(event::setDescription);


        if (newEvent.getEventDate() != null) {
            event.setEventDate(newEvent.getEventDate());
        }

        if (newEvent.getLocation() != null) {
            //Сохраняем локацию в БД
            LocationDb locationDb = locationService.addLocation(newEvent.getLocation());
            event.setLocation(locationDb);
        }

        Optional.ofNullable(newEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(newEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(newEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(newEvent.getTitle()).ifPresent(event::setTitle);

        if (newEvent.getStateAction() == UpdateEventUserRequest.StateAction.CANCEL_REVIEW) {
            event.setState(Event.State.CANCELED);
        } else if (newEvent.getStateAction() == UpdateEventUserRequest.StateAction.SEND_TO_REVIEW) {
            event.setState(Event.State.PENDING);
        }

        log.info("Обновили событие с id: {}", eventId);

        eventRepository.save(event);

        return event;
    }
}
