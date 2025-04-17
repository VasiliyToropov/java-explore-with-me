package ru.practicum.publicuser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.model.compilation.Compilation;
import ru.practicum.publicuser.service.PublicCompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final PublicCompilationService compilationService;

    @GetMapping
    public List<Compilation> getCompilations(@RequestParam(required = false) Boolean pinned,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public Compilation getCompilation(@PathVariable Long compId) {
        return compilationService.getCompilation(compId);
    }
}
