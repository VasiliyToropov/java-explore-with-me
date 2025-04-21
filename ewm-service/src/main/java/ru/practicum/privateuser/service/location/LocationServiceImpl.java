package ru.practicum.privateuser.service.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.privateuser.model.location.Location;
import ru.practicum.privateuser.model.location.LocationDb;
import ru.practicum.privateuser.repository.LocationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationDb addLocation(Location location) {

        LocationDb locationDb = new LocationDb();
        locationDb.setLat(location.getLat());
        locationDb.setLon(location.getLon());

        log.info("Добавлена новая локация");

        locationRepository.save(locationDb);

        return locationDb;
    }
}
