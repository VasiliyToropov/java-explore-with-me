package ru.practicum.admin.service.event;

import ru.practicum.admin.model.request.UpdateEventAdminRequest;
import ru.practicum.privateuser.model.event.Event;

import java.util.List;

public interface AdminEventService {

    List<Event> getEventsForAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    Event updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
