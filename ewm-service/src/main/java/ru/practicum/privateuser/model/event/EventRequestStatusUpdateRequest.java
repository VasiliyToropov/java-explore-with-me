package ru.practicum.privateuser.model.event;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    @Pattern(regexp = "^(CONFIRMED|REJECTED)$")
    private String status;

}
