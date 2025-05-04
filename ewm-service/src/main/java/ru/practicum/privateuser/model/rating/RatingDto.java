package ru.practicum.privateuser.model.rating;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingDto {

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long eventId;

    @Pattern(regexp = "^(LIKE|DISLIKE)$")
    String ratingType;
}
