package ru.practicum.privateuser.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.user.User;
import ru.practicum.admin.service.user.UserServiceImpl;
import ru.practicum.exceptions.exception.ForbiddenException;
import ru.practicum.exceptions.exception.NotFoundException;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.EventRequestStatusUpdateRequest;
import ru.practicum.privateuser.model.event.EventRequestStatusUpdateResult;
import ru.practicum.privateuser.model.request.ParticipationRequest;
import ru.practicum.privateuser.repository.EventRepository;
import ru.practicum.privateuser.repository.RequestRepository;
import ru.practicum.privateuser.service.event.EventServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final EventServiceImpl eventService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserServiceImpl userService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ParticipationRequest addRequestByParticipiant(Long userId, Long eventId) {

        Event event = eventService.getEventByEventId(eventId);

        //Нельзя участвовать в неопубликованном событии
        if (event.getState() != Event.State.PUBLISHED) {
            throw new ForbiddenException("User cannot participate in an unpublished event");
        }

        //Если у события достигнут лимит подтвержденных запросов на участие - необходимо вернуть ошибку
        if (event.getConfirmedRequests() != 0 && event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ForbiddenException("The limit of participation requests has been reached");
        }


        //Инициатор события не может добавить запрос на участие в своём событии
        if (event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("The initiator of the event cannot add a request to participate in his event");
        }

        //Пользователь не может добавить повторный запрос на участие в событии
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        for (ParticipationRequest request : requests) {
            if (Objects.equals(request.getRequester(), userId)) {
                throw new ForbiddenException("The user cannot add a repeat request to participate in the event.");
            }
        }

        // Если количество запросов достигло лимита, то добавить новый запрос нельзя
        if (event.getParticipantLimit() != 0 && requests.size() == event.getParticipantLimit()) {
            throw new ForbiddenException("The number of requests has reached the limit");
        }


        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now().format(FORMATTER));
        participationRequest.setEvent(event.getId());
        participationRequest.setRequester(userId);
        participationRequest.setStatus(ParticipationRequest.Status.PENDING);

        //Если для события отключена пре-модерация запросов на участие или лимит равен 0,
        //то запрос должен автоматически перейти в состояние подтвержденного
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(ParticipationRequest.Status.CONFIRMED);
        }

        log.info("Добавили новый запрос от пользователя {} для события {}", userId, eventId);

        requestRepository.save(participationRequest);

        return participationRequest;
    }

    @Override
    public List<ParticipationRequest> getRequestByParticipiant(Long userId) {

        log.info("Получили все запросы от пользователя с id: {}", userId);

        return requestRepository.findAllByRequester(userId);
    }

    @Override
    public ParticipationRequest cancelRequestByParticipiant(Long userId, Long requestId) {
        Optional<ParticipationRequest> foundedRequest = requestRepository.findById(requestId);
        ParticipationRequest request = foundedRequest.orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        request.setStatus(ParticipationRequest.Status.CANCELED);

        requestRepository.save(request);

        log.info("Отмена запроса {} от пользователя с id: {}", requestId, userId);

        return request;
    }

    @Override
    public List<ParticipationRequest> getRequestsByUserAndEventId(Long userId, Long eventId) {

        User user = userService.getUserById(userId);
        Event event = eventService.getEventByEventId(eventId);

        //Проверяем, что пользователь инициатор события
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new ForbiddenException("The user is not the initiator of the event");
        }

        log.info("Получили запросы для пользователя {} для события {}", userId, eventId);

        return requestRepository.findAllByEventId(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult changeEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateDto) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        User user = userService.getUserById(userId);
        Event event = eventService.getEventByEventId(eventId);

        //Пользователь не может быть инициатором события
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new ForbiddenException("The user cannot be the initiator of the event");
        }

        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ForbiddenException("Confirmation of applications is not required for this event.");
        }
        List<ParticipationRequest> requests = requestRepository.findAllById(statusUpdateDto.getRequestIds());

        long available = event.getParticipantLimit() - event.getConfirmedRequests();
        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        for (ParticipationRequest request : requests) {

            //Можно обрабатывать только заявки в статусе PENDING
            if (!request.getStatus().equals(ParticipationRequest.Status.PENDING)) {
                throw new ForbiddenException("Only PENDING applications can be processed");
            }

            if (Objects.equals(statusUpdateDto.getStatus(), ParticipationRequest.Status.CONFIRMED.toString())) {
                // Если достигнут лимит участников
                if (available <= 0) {
                    throw new ForbiddenException("The limit of participants has been reached. Confirmation is not possible.");
                }

                request.setStatus(ParticipationRequest.Status.CONFIRMED);
                available--;
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmed.add(request);
            } else if (Objects.equals(statusUpdateDto.getStatus(), ParticipationRequest.Status.REJECTED.toString())) {
                request.setStatus(ParticipationRequest.Status.REJECTED);
                rejected.add(request);
            }
        }
        log.info("успешное обновления статуса события");

        requestRepository.saveAll(requests);
        eventRepository.save(event);

        result.setConfirmedRequests(confirmed);
        result.setRejectedRequests(rejected);

        log.info("Добавлены запросы к событиям");

        return result;
    }

    public ParticipationRequest getRequestById(Long requestId) {
        Optional<ParticipationRequest> foundedRequest = requestRepository.findById(requestId);
        return foundedRequest.orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
    }


}
