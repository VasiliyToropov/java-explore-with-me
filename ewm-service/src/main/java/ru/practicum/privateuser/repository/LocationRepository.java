package ru.practicum.privateuser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.privateuser.model.location.LocationDb;

@Repository
public interface LocationRepository extends JpaRepository<LocationDb, Long> {
}
