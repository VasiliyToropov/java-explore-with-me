package ru.practicum.publicuser.service;


import ru.practicum.admin.model.compilation.Compilation;

import java.util.List;

public interface PublicCompilationService {
    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size);

    Compilation getCompilation(Long compId);
}
