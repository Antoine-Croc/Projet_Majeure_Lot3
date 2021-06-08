
package com.project.repository;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.project.model.Vehicle;

public interface VehicleRepository extends CrudRepository<Vehicle, Integer> {
	
	//public List<Vehicle> findByType(VehicleType type);
	public Optional<Vehicle> findOneByIdDto(Integer idDto);

	

}

