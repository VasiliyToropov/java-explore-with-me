package ru.practicum.privateuser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.privateuser.model.request.ParticipationRequest;
import ru.practicum.privateuser.service.request.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequest addRequestByParticipiant(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.addRequestByParticipiant(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequest> getRequestByParticipiant(@PathVariable Long userId) {
        return requestService.getRequestByParticipiant(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequest cancelRequestByParticipiant(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequestByParticipiant(userId, requestId);
    }
}
