package com.project.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.project.service.VehicleService;
import com.project.model.Vehicle;
import com.project.model.dto.VehicleDto;


@RestController
public class VehicleRestCtrl {
	@Autowired
	VehicleService vService;
	
	
	@RequestMapping(method=RequestMethod.POST,value = "/vehicles",consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public Vehicle addVehicle(@RequestBody VehicleDto vehicleDTO ) {
		Vehicle vehicle = new Vehicle(vehicleDTO);
		
		String urlSimulator = "localhost:8081/vehicle";
		//ajouter le vehicule dans le FireSimulator
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<VehicleDto> request = new HttpEntity<>(vehicleDTO);
		ResponseEntity<VehicleDto> response = restTemplate
		  .exchange(urlSimulator, HttpMethod.POST, request, VehicleDto.class);
		 
		//mettre a jour IdDTO de la classe Vehicle
		VehicleDto responseVehicle = response.getBody();
		vehicle.setIdDto(responseVehicle.getId());
		vService.addVehicle(vehicle);
	
		System.out.println("add vehicle : "+ responseVehicle);
		return vehicle;
	}
	
	@RequestMapping(method=RequestMethod.GET, value = )
	public Vehicle getVehicleById() {
		
		
	}
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = "/vehicles/{id}")
	public void deleteVehicle(@PathVariable String id ) {
		vService.deleteVehicleById(Integer.valueOf(id));
	}
	
	

}
