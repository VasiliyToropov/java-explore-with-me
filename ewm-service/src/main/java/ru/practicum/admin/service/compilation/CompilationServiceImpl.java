package ru.practicum.admin.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.compilation.Compilation;
import ru.practicum.admin.model.compilation.NewCompilationDto;
import ru.practicum.admin.model.compilation.UpdateCompilationRequest;
import ru.practicum.admin.repository.CompilationRepository;
import ru.practicum.exceptions.exception.NotFoundException;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.service.event.EventServiceImpl;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final EventServiceImpl eventService;
    private final CompilationRepository compilationRepository;

    @Override
    public Compilation addNewCompilation(NewCompilationDto newCompilationDto) {


        List<Event> events = new ArrayList<>();
        Compilation compilation = new Compilation();

        if (newCompilationDto.getEvents() != null) {
            Set<Long> uniqueEventIds = new HashSet<>(newCompilationDto.getEvents());
            for (Long id : uniqueEventIds) {
                events.add(eventService.getEventByEventId(id));
            }
            compilation.setEvents(events);
        }

        // Устанавливаем pinned с дефолтным значением false
        compilation.setPinned(newCompilationDto.getPinned() != null ?
                newCompilationDto.getPinned() : false);

        compilation.setTitle(newCompilationDto.getTitle());

        compilationRepository.save(compilation);

        log.info("Добавлена новая подборка: {}", compilation.getTitle());

        return compilation;
    }

    @Override
    public void deleteCompilation(Long compId) {
        Optional<Compilation> foundedCompilationDto = compilationRepository.findById(compId);

        Compilation compilation = foundedCompilationDto.orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        log.info("Удалена подборка с id: {}", compId);

        compilationRepository.delete(compilation);
    }

    @Override
    public Compilation updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilation(compId);

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            for (Long id : updateCompilationRequest.getEvents()) {
                events.add(eventService.getEventByEventId(id));
            }
            compilation.setEvents(events);
        }

        Optional.ofNullable(updateCompilationRequest.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilationRequest.getPinned()).ifPresent(compilation::setPinned);

        compilationRepository.save(compilation);

        log.info("Обновлена подборка с id: {}", compId);

        return compilation;
    }

    @Override
    public Compilation getCompilation(Long compId) {
        Optional<Compilation> foundedCompilationDto = compilationRepository.findById(compId);

        log.info("Получена подборка с id: {}", compId);

        return foundedCompilationDto.orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
    }
}
