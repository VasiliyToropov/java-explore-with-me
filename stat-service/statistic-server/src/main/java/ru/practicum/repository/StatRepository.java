package ru.practicum.repository;

import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {


    @Query("SELECT new ru.practicum.model.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp BETWEEN :startTime AND :endTime " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    public List<ViewStats> getStatsUnique(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT new ru.practicum.model.ViewStats(e.app, e.uri, COUNT(e)) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp BETWEEN :startTime AND :endTime " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e) DESC")
    public List<ViewStats> getStatsNotUnique(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);
}
