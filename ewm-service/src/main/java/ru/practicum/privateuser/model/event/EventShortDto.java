package ru.practicum.privateuser.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.admin.model.category.Category;
import ru.practicum.admin.model.user.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private Long id;

    private String annotation;

    private Category category;

    private int confirmedRequests;

    private String description;

    private String eventDate;

    private User initiator;

    private boolean paid;

    private String title;

    private int views;

    private Long likes;

    private Long dislikes;
}
