package ru.practicum.privateuser.service.request;

import ru.practicum.privateuser.model.event.EventRequestStatusUpdateRequest;
import ru.practicum.privateuser.model.event.EventRequestStatusUpdateResult;
import ru.practicum.privateuser.model.request.ParticipationRequest;

import java.util.List;

public interface RequestService {
    ParticipationRequest addRequestByParticipiant(Long userId, Long eventId);

    List<ParticipationRequest> getRequestByParticipiant(Long userId);

    ParticipationRequest cancelRequestByParticipiant(Long userId, Long requestId);

    List<ParticipationRequest> getRequestsByUserAndEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeEventRequestStatus(Long userId,
                                                            Long eventId,
                                                            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
