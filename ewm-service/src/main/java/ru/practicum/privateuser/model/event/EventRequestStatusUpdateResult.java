package ru.practicum.privateuser.model.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.privateuser.model.request.ParticipationRequest;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequest> confirmedRequests;
    private List<ParticipationRequest> rejectedRequests;
}
