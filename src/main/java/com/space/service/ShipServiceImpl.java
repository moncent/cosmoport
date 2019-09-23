package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public Ship createShip(Ship ship) throws IllegalArgumentException {
        if (ship.getName() == null || ship.getName().isEmpty() || ship.getName().length() > 50) {
            throw new IllegalArgumentException("Wrong format of the ship name!");
        }
        if (ship.getPlanet() == null || ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50) {
            throw new IllegalArgumentException("Wrong format of the planet!");
        }

        if (ship.getProdDate().getTime() < 0 || ship.getProdDate() == null) {
            throw new IllegalArgumentException("The date is less than 0 or null!");
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(ship.getProdDate().getTime());
        if (calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019) {
            throw new IllegalArgumentException("The date is out of the range!");
        }

        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }

        if (ship.getSpeed() == null) {
            throw new IllegalArgumentException("Wrong speed!");
        }

        if (ship.getSpeed() >= 0.01d && ship.getSpeed() <= 0.99d) {
            double speed = ship.getSpeed();
            speed = Math.round(speed * 100) / 100d;
            ship.setSpeed(speed);
        } else {
            throw new IllegalArgumentException("Wrong speed!");
        }

        if (ship.getCrewSize() == null || ship.getCrewSize() <= 0 || ship.getCrewSize() > 9999) {
            throw new IllegalArgumentException("Wrong crew size!");
        }

        ship.setRating(calculateRating(ship.getUsed(), ship.getSpeed(), calendar.get(Calendar.YEAR)));
        return shipRepository.saveAndFlush(ship);
    }

    private double calculateRating(boolean isUsed, double speed, int year) {
        double k = (isUsed) ? 0.5 : 1;
        double rating = 80 * speed * k / (3019 - year + 1);
        return Math.round(rating * 100) / 100d;
    }

    @Override
    public Page<Ship> getAllShips(Pageable pageable, Specification<Ship> specification) {
        return shipRepository.findAll(specification, pageable);
    }

    public Specification<Ship> filterName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            } else {
               return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }

    @Override
    public Specification<Ship> filterPlanet(String planet) {
        return (root, query, criteriaBuilder) -> {
            if (planet == null) {
                return null;
            } else {
                return criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
            }
        };
    }

    @Override
    public Specification<Ship> filterShipType(ShipType shipType) {
        return (root, query, criteriaBuilder) -> {
          if (shipType == null) {
              return null;
          } else {
              return criteriaBuilder.equal(root.get("shipType"), shipType);
          }
        };
    }

    @Override
    public Specification<Ship> filterDate(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                Date beforeDate = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), beforeDate);
            }
            if (before == null) {
                Date afterDate = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), afterDate);
            }
            Date beforeDate = new Date(before);
            Date afterDate = new Date(after);
            return criteriaBuilder.between(root.get("prodDate"), afterDate, beforeDate);
        };
    }

    @Override
    public Specification<Ship> filterUsed(Boolean isUsed) {
        return (root, query, criteriaBuilder) -> {
            if (isUsed == null) {
                return null;
            }
            if (isUsed) {
                return criteriaBuilder.isTrue(root.get("isUsed"));
            }
            return criteriaBuilder.isFalse(root.get("isUsed"));
        };
    }

    @Override
    public Specification<Ship> filterSpeed(Double minSpeed, Double maxSpeed) {
        return (root, query, criteriaBuilder) -> {
            if (minSpeed == null && maxSpeed == null) {
                return null;
            }
            if (minSpeed == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed);
            }
            if (maxSpeed == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed);
            }
            return criteriaBuilder.between(root.get("speed"), minSpeed, maxSpeed);
        };
    }

    @Override
    public Specification<Ship> filterCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        return (root, query, criteriaBuilder) -> {
            if (minCrewSize == null && maxCrewSize == null) {
                return null;
            }
            if (minCrewSize == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize);
            }
            if (maxCrewSize == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize);
            }
            return criteriaBuilder.between(root.get("crewSize"), minCrewSize, maxCrewSize);
        };
    }

    @Override
    public Specification<Ship> filterRating(Double minRating, Double maxRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null && maxRating == null) {
                return null;
            }
            if (minRating == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating);
            }
            if (maxRating == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);
            }
            return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
        };
    }


    @Override
    public Integer getShipsCount(Specification<Ship> specification) {
        return (int)shipRepository.count(specification);
    }

    @Override
    public Ship getShipById(Long id) throws IllegalArgumentException {
        if (id <= 0) {
            throw new IllegalArgumentException("Wrong id!");
        }
        if (shipRepository.existsById(id)) {
            Optional<Ship> ship = shipRepository.findById(id);
            if (ship.isPresent()) {
                return ship.get();
            }
        }
        throw new IllegalArgumentException("The ship is absent!");
    }

    @Override
    public Ship updateShip(Ship ship) throws IllegalArgumentException {
        if (ship.getId() <= 0) {
            throw new IllegalArgumentException("Wrong id!");
        }

        if (!shipRepository.existsById(ship.getId())) {
            throw new IllegalArgumentException("The ship is absent!");
        }

        Ship editShip = shipRepository.findById(ship.getId()).get();

        if (ship.getName() == null && ship.getPlanet() == null && ship.getShipType() == null && ship.getProdDate() == null
        && ship.getUsed() == null && ship.getSpeed() == null && ship.getCrewSize() == null) {
            return editShip;
        }

        if (ship.getName() != null) {
            if (!ship.getName().isEmpty())
                editShip.setName(ship.getName());
            else {
                throw new IllegalArgumentException("bad request!");
            }
        }


        if (ship.getPlanet() != null) {
            editShip.setPlanet(ship.getPlanet());
        }
        if (ship.getShipType() != null) {
            editShip.setShipType(ship.getShipType());
        }

        Calendar calendar = new GregorianCalendar();
        if (ship.getProdDate() != null) {
            calendar.setTimeInMillis(ship.getProdDate().getTime());
            if (calendar.get(Calendar.YEAR) >= 2800 && calendar.get(Calendar.YEAR) <= 3019) {
                editShip.setProdDate(ship.getProdDate());
            } else {
                throw new IllegalArgumentException("bad request!");
            }
        }
        if (ship.getUsed() != null) {
            editShip.setUsed(ship.getUsed());
        }
        if (ship.getSpeed() != null) {
            editShip.setSpeed(ship.getSpeed());
        }
        if (ship.getCrewSize() != null) {
            if (ship.getCrewSize() > 0 && ship.getCrewSize() <= 9999)
                editShip.setCrewSize(ship.getCrewSize());
            else
                throw new IllegalArgumentException("bad request!");
        }
        calendar.setTimeInMillis(editShip.getProdDate().getTime());
        editShip.setRating(calculateRating(editShip.getUsed(), editShip.getSpeed(), calendar.get(Calendar.YEAR)));
        return shipRepository.save(editShip);

    }

    @Override
    public void deleteShipById(Long id) throws IllegalArgumentException {
        if (id <= 0) {
            throw new IllegalArgumentException("Wrong id!");
        }
        if (shipRepository.existsById(id)) {
            Optional<Ship> ship = shipRepository.findById(id);
            ship.ifPresent(value -> shipRepository.delete(value));
            return;
        }
        throw new IllegalArgumentException("The ship is absent!");
    }
}
