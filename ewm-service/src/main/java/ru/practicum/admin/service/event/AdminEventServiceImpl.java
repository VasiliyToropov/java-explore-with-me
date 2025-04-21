package ru.practicum.admin.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.category.Category;
import ru.practicum.admin.model.request.UpdateEventAdminRequest;
import ru.practicum.admin.repository.AdminEventRepository;
import ru.practicum.admin.service.category.CategoryServiceImpl;
import ru.practicum.exceptions.exception.BadRequestException;
import ru.practicum.exceptions.exception.ForbiddenException;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.location.LocationDb;
import ru.practicum.privateuser.service.event.EventServiceImpl;
import ru.practicum.privateuser.service.location.LocationServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminEventServiceImpl implements AdminEventService {

    private final AdminEventRepository adminEventRepository;
    private final EventServiceImpl eventService;
    private final CategoryServiceImpl categoryService;
    private final LocationServiceImpl locationService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Event> getEventsForAdmin(List<Long> users,
                                         List<String> states,
                                         List<Long> categories,
                                         String rangeStart,
                                         String rangeEnd,
                                         Integer from,
                                         Integer size) {

        // Если from и size не заданы, то устанавливаем значения по умолчанию
        int defaultFrom = (from == null) ? 0 : from;
        int defaultSize = (size == null) ? 10 : size;

        Pageable pageable = PageRequest.of(defaultFrom / defaultSize, defaultSize);

        Page<Event> page = adminEventRepository.getEventsForAdmin(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                pageable);

        log.info("Получение списка событий для администратора");

        return page.getContent();
    }

    @Override
    public Event updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {

        Event event = eventService.getEventByEventId(eventId);

        if (updateEvent.getCategory() != null) {
            Category category = categoryService.getCategoryById(updateEvent.getCategory());
            event.setCategory(category);
        }

        //Дата начала изменяемого события должна быть не ранее, чем за час от даты публикации.
        if (updateEvent.getEventDate() != null) {

            LocalDateTime eventDate = LocalDateTime.parse(event.getEventDate(), FORMATTER);
            LocalDateTime updatedEventDate = LocalDateTime.parse(updateEvent.getEventDate(), FORMATTER);

            if (eventDate.plusHours(1).isBefore(updatedEventDate)) {
                throw new ForbiddenException("The start date of the event to be modified must be no earlier than one hour from the publication date.");
            }
            String updatedEventDateString = updatedEventDate.format(FORMATTER);

            //Нельзя изменить дату события на уже наступившую
            if (updatedEventDate.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("You can't change events that have already happened");
            }

            event.setEventDate(updatedEventDateString);
        }

        if (updateEvent.getLocation() != null) {
            LocationDb locationDb = locationService.addLocation(updateEvent.getLocation());
            event.setLocation(locationDb);
        }

        Optional.ofNullable(updateEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEvent.getTitle()).ifPresent(event::setTitle);

        //Событие можно публиковать, только если оно в состоянии ожидания публикации
        if (updateEvent.getStateAction() == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
            if (event.getState() == Event.State.PENDING) {
                event.setState(Event.State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now().format(FORMATTER));
                return adminEventRepository.save(event);
            } else {
                throw new ForbiddenException("Cannot publish the event because it's not in the right state: PENDING");
            }
        }

        //Событие можно отклонить, только если оно еще не опубликовано
        if (updateEvent.getStateAction() == UpdateEventAdminRequest.StateAction.REJECT_EVENT) {
            if (event.getState() == Event.State.PUBLISHED) {
                throw new ForbiddenException("The event cannot be rejected because it is in the PUBLISHED state.");
            } else {
                event.setState(Event.State.CANCELED);
                return adminEventRepository.save(event);
            }
        }

        log.info("Событие с id: {} отклонено", eventId);

        return event;
    }
}
