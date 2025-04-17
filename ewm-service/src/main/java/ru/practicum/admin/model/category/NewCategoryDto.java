package ru.practicum.admin.model.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCategoryDto {

    @Size(min = 1, max = 50)
    @NotNull
    @NotBlank
    private String name;
}
