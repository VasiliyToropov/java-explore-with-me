package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Qualifier("StatServiceImpl")
@Transactional
@Slf4j
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointHit postEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();

        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setTimestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(), FORMATTER));

        statRepository.save(endpointHit);

        log.info("Сохранение в статистике обращение к эндпоинту: {}", endpointHit.getUri());

        return endpointHit;
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);

        //Проверяем корректный диапазон времени
        if (startTime.isAfter(endTime) || startTime.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Некорректный диапазон времени");
        }

        log.info("Получение статистики использования эндпоинтов: {}", uris);

        //Если unique не задано, то присваиваем false
        if (unique == null) {
            unique = false;
        }

        if (unique) {
            return statRepository.getStatsUnique(startTime, endTime, uris);
        } else {
            return statRepository.getStatsNotUnique(startTime, endTime, uris);
        }

    }
}
