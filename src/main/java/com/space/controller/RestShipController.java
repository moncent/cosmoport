package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class RestShipController {

    @Autowired
    private ShipService shipService;

    @GetMapping("/ships")
    @ResponseStatus(HttpStatus.OK)
    public List<Ship> getAllShips(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "planet", required = false) String planet,
                                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                                  @RequestParam(value = "after", required = false) Long after,
                                  @RequestParam(value = "before", required = false) Long before,
                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                  @RequestParam(value = "minRating", required = false) Double minRating,
                                  @RequestParam(value = "maxRating", required = false) Double maxRating,
                                  @RequestParam(value = "order", defaultValue = "ID", required = false) ShipOrder order,
                                  @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                  @RequestParam(value = "pageSize", defaultValue = "3" ,required = false) Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Page<Ship> shipPage = shipService.getAllShips(pageable, Specification.where(
                shipService.filterName(name)
                .and(shipService.filterPlanet(planet))
                .and(shipService.filterShipType(shipType)
                .and(shipService.filterDate(after, before))
                .and(shipService.filterUsed(isUsed))
                .and(shipService.filterSpeed(minSpeed, maxSpeed))
                .and(shipService.filterCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterRating(minRating, maxRating))
                )));

        return shipPage.getContent();
    }

    @GetMapping("/ships/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getShipsCount(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "planet", required = false) String planet,
                                 @RequestParam(value = "shipType", required = false) ShipType shipType,
                                 @RequestParam(value = "after", required = false) Long after,
                                 @RequestParam(value = "before", required = false) Long before,
                                 @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                 @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                 @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                 @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                 @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                 @RequestParam(value = "minRating", required = false) Double minRating,
                                 @RequestParam(value = "maxRating", required = false) Double maxRating) {

        return shipService.getShipsCount(Specification.where(
                shipService.filterName(name)
                .and(shipService.filterPlanet(planet))
                .and(shipService.filterShipType(shipType)
                .and(shipService.filterDate(after, before))
                .and(shipService.filterUsed(isUsed))
                .and(shipService.filterSpeed(minSpeed, maxSpeed))
                .and(shipService.filterCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterRating(minRating, maxRating))
                )));
    }

    @PostMapping("/ships")
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {
        try {
            shipService.createShip(ship);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(ship);
    }

    @GetMapping("/ships/{id}")
    public ResponseEntity<Ship> getShipById(@PathVariable("id") Long id) {
        Ship ship;
        try {
            ship = shipService.getShipById(id);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Wrong id!")) {
                return ResponseEntity.badRequest().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(ship);
    }

    @PostMapping("/ships/{id}")
    public ResponseEntity<Ship> updateShipById(@PathVariable("id") Long id, @RequestBody Ship ship) {
        Ship editShip;
        try {
            ship.setId(id);
            editShip = shipService.updateShip(ship);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Wrong id!") || e.getMessage().equals("bad request!")) {
                return ResponseEntity.badRequest().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(editShip);
    }

    @DeleteMapping("/ships/{id}")
    public ResponseEntity<Ship> deleteShipById(@PathVariable("id") Long id) {
        try {
            shipService.deleteShipById(id);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Wrong id!")) {
                return ResponseEntity.badRequest().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok().build();
    }
}