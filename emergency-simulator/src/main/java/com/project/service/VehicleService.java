package com.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.project.model.Vehicle;
import com.project.repository.VehicleRepository;

@Service
public class VehicleService {

	@Autowired 
	private VehicleRepository vRepository;
	
	public void  addVehicle(Vehicle vehicle) {
		Vehicle createdVehicle = vRepository.save(vehicle);
		
		System.out.println(createdVehicle);
	}
	
	
	public Vehicle getVehicleById(int id) {
		Optional<Vehicle> hOpt = vRepository.findById(id);
		if(hOpt.isPresent()) {
			return hOpt.get();
		}
		else {return null;}
		
	}
}
