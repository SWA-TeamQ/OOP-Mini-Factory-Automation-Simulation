package org.Automation.repositories;

import org.Automation.entities.Station;
import java.util.*;

public class StationRepository {
    private Map<String, Station> stations = new HashMap<>();

    public void addStation(Station station) {
        stations.put(station.getStationId(), station);
        System.out.println("✅ Registered: " + station.getName());
    }

    public Station getStation(String id) {
        return stations.get(id);
    }

    public List<Station> getAllStations() {
        return new ArrayList<>(stations.values());
    }
}
