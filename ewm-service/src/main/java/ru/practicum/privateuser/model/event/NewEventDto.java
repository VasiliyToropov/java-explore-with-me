package ru.practicum.privateuser.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.privateuser.model.location.Location;

@Getter
@Setter
public class NewEventDto {
    @Size(min = 20, max = 2000)
    @NotNull
    @NotBlank
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    @NotNull
    @NotBlank
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;

    @NotNull
    private Location location;

    @NotNull
    private boolean paid;

    @NotNull
    @PositiveOrZero
    private int participantLimit;

    @NotNull
    private boolean requestModeration = true;

    @Size(min = 3, max = 120)
    private String title;

}
