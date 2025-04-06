package ru.practicum.service;

import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatService {
    public EndpointHit postEndpointHit(EndpointHitDto endpointHitDto);

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
