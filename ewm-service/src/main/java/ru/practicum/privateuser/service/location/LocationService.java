package ru.practicum.privateuser.service.location;

import ru.practicum.privateuser.model.location.Location;
import ru.practicum.privateuser.model.location.LocationDb;

public interface LocationService {
    public LocationDb addLocation(Location location);
}
