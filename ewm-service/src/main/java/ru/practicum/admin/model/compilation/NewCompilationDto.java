package ru.practicum.admin.model.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewCompilationDto {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    @NotNull
    @NotBlank
    private String title;
}
