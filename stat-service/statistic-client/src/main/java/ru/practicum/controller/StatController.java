package ru.practicum.controller;

import ru.practicum.client.StatClient;
import lombok.RequiredArgsConstructor;
import ru.practicum.model.EndpointHitDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatController {

    private final StatClient statClient;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> postEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        return statClient.postEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getStats(@RequestParam String start, @RequestParam String end, @RequestParam List<String> uris, @RequestParam Boolean unique) {
        return statClient.getStats(start, end, uris, unique);
    }
}
