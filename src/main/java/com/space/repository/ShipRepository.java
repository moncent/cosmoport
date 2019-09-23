package com.space.repository;

import com.space.model.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ShipRepository extends JpaRepository<Ship, Long>, PagingAndSortingRepository<Ship, Long>, JpaSpecificationExecutor<Ship> {
}
