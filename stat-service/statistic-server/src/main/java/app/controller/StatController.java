package app.controller;

import app.model.EndpointHit;
import app.service.StatService;
import lombok.RequiredArgsConstructor;
import model.EndpointHitDto;
import model.ViewStats;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit postEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        return statService.postEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(@RequestParam String start, @RequestParam String end, @RequestParam List<String> uris, @RequestParam Boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }
}
