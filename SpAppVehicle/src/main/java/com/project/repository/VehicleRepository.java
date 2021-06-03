
package com.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.project.model.Vehicle;
import com.project.model.dto.VehicleType;

public interface VehicleRepository extends CrudRepository<Vehicle, Integer> {
	
	//public List<Vehicle> findByType(VehicleType type);
	public Optional<Vehicle> findOneByIdDto(Integer idDto);

}

