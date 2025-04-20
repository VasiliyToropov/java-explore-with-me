package ru.practicum.publicuser.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.privateuser.model.event.Event;
import ru.practicum.privateuser.model.event.EventShortDto;

import java.util.List;

@Repository
public interface PublicEventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    default List<EventShortDto> getEventsByPublicUser(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Pageable pageable) {

        Specification<Event> spec = (root, query, cb) -> {
            query.distinct(true);
            Predicate predicate = cb.equal(root.get("state"), Event.State.PUBLISHED);

            if (text != null) {
                Predicate textPredicate = cb.or(
                        cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                );
                predicate = cb.and(predicate, textPredicate);
            }

            if (categories != null && !categories.isEmpty()) {
                predicate = cb.and(predicate, root.get("category").get("id").in(categories));
            }

            if (paid != null) {
                predicate = cb.and(predicate, cb.equal(root.get("paid"), paid));
            }

            if (rangeStart != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }

            if (rangeEnd != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }

            if (Boolean.TRUE.equals(onlyAvailable)) {
                Predicate availablePredicate = cb.or(
                        cb.equal(root.get("participantLimit"), 0),
                        cb.lessThan(root.get("confirmedRequests"), root.get("participantLimit"))
                );
                predicate = cb.and(predicate, availablePredicate);
            }

            return predicate;
        };

        // Сортировка
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    return findAll(spec, pageable).map(this::toEventShortDto).getContent();
                case "VIEWS":
                    return findAll(spec, pageable).map(this::toEventShortDto).getContent();
                default:
                    return findAll(spec, pageable).map(this::toEventShortDto).getContent();
            }
        }

        return findAll(spec, pageable).map(this::toEventShortDto).getContent();
    }

    private EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getDescription(),
                event.getEventDate(),
                event.getInitiator(),
                event.isPaid(),
                event.getTitle(),
                event.getViews()
        );
    }
}
