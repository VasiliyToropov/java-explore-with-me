package ru.practicum.admin.service.compilation;

import ru.practicum.admin.model.compilation.Compilation;
import ru.practicum.admin.model.compilation.NewCompilationDto;
import ru.practicum.admin.model.compilation.UpdateCompilationRequest;

public interface CompilationService {
    public Compilation addNewCompilation(NewCompilationDto newCompilationDto);

    public void deleteCompilation(Long compId);

    public Compilation updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    public Compilation getCompilation(Long compId);
}
