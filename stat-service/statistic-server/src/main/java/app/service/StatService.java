package app.service;

import app.model.EndpointHit;
import model.EndpointHitDto;
import model.ViewStats;

import java.util.List;

public interface StatService {
    public EndpointHit postEndpointHit(EndpointHitDto endpointHitDto);

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
