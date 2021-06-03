package com.project.service;

import java.util.ArrayList;
import java.util.List;
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
		
		
	}
	
	
	public Vehicle getVehicleById(Integer id) {
		Optional<Vehicle> hOpt = vRepository.findById(id);
		if(hOpt.isPresent()) {
			return hOpt.get();
		}
		else {return null;}
		
	}
	
	public Vehicle getVehicleByIdDto(Integer id) {
		Optional<Vehicle> hOpt = vRepository.findOneByIdDto(id);
		if(hOpt.isPresent()) {
			return hOpt.get();
		}
		else {return null;}
		
	}
	
	public List<Vehicle> getAllVehicles(){
		List<Vehicle> vehicles = new ArrayList<>();
		vRepository.findAll().forEach(vehicles::add);
		return vehicles;
	} 
	
	
	
	public void deleteVehicleById(Integer id) {
		vRepository.deleteById(id);
		System.out.println("delete vehicle id:"+id);
	}
	
	
	
	
}
