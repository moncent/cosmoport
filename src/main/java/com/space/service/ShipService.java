package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface ShipService {
    Ship createShip(Ship ship);
    Page<Ship> getAllShips(Pageable pageable, Specification<Ship> specification);
    Integer getShipsCount(Specification<Ship> specification);

    Specification<Ship> filterName(String name);
    Specification<Ship> filterPlanet(String planet);
    Specification<Ship> filterShipType(ShipType shipType);
    Specification<Ship> filterDate(Long after, Long before);
    Specification<Ship> filterUsed(Boolean isUsed);
    Specification<Ship> filterSpeed(Double minSpeed, Double maxSpeed);
    Specification<Ship> filterCrewSize(Integer minCrewSize, Integer maxCrewSize);
    Specification<Ship> filterRating(Double minRating, Double maxRating);

    Ship getShipById(Long id);
    Ship updateShip(Ship ship);
    void deleteShipById(Long id);
}
