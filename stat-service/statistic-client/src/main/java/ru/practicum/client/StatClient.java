package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStats;

import java.util.List;

@Slf4j
@Component
public class StatClient {
    private final RestClient restClient;
    private final String baseUrl;

    public StatClient(@Value("${stats-server.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public EndpointHitDto postEndpointHit(EndpointHitDto endpointHitDto) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/hit")
                .build()
                .toUriString();
        log.info("POST to URI: {}", uri);

        return restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto)
                .retrieve()
                .body(EndpointHitDto.class);  // Десериализуем в DTO
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        String fullUri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();

        log.info("GET to URI: {}", fullUri);

        try {
            return restClient.get()
                    .uri(fullUri)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new RuntimeException("Client error: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new RuntimeException("Server error: " + res.getStatusCode());
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.error("Error while getting stats", e);
            return List.of();
        }
    }
}
