package ru.practicum.publicuser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.model.compilation.Compilation;
import ru.practicum.exceptions.exception.NotFoundException;
import ru.practicum.publicuser.repository.PublicCompilationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final PublicCompilationRepository compilationRepository;

    @Override
    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {

        int defaultFrom = (from == null) ? 0 : from;
        int defaultSize = (size == null) ? 10 : size;

        Pageable pageable = PageRequest.of(defaultFrom / defaultSize, defaultSize);

        log.info("Получили компиляции с выборкой");

        return compilationRepository.getCompilations(pinned, pageable);
    }

    @Override
    public Compilation getCompilation(Long compId) {

        Optional<Compilation> foundedCompilation = compilationRepository.findById(compId);

        log.info("Получили компиляцию с id: {}", compId);

        return foundedCompilation.orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
    }
}
