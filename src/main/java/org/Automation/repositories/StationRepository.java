package org.Automation.repositories;

import org.Automation.entities.Station;

public class StationRepository extends Repository<Station> {

    @Override
    public void save(Station station) { add(station.getId(), station); }

    @Override
    public void delete(String id) { remove(id); }
}
