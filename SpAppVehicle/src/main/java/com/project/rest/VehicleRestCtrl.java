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
import org.springframework.web.bind.annotation.RequestParam;
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
		
		String urlSimulator = "http://localhost:8081/vehicle";
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
	
	@RequestMapping(method=RequestMethod.GET, value = "/vehicles/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
	public Vehicle getVehicleById(@PathVariable String id) {
		Vehicle vehicle = vService.getVehicleById(Integer.valueOf(id));
		return vehicle;
	}
	
	//retourner tous les vehicules depuis FireSimulator
	@RequestMapping(method=RequestMethod.GET, value = "/vehicles",produces = MediaType.APPLICATION_JSON_VALUE)
	public VehicleDto[] getAllVehicle() {
		
		//faire la requete vers FireSimulator
		String urlSimulator = "http://localhost:8081/vehicle";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<VehicleDto[]> response = restTemplate.exchange(urlSimulator, HttpMethod.GET,null,VehicleDto[].class);
		
		//mettre le repository a jour 
		VehicleDto[] vehiclesDto = response.getBody();
		for(VehicleDto vehicleDto : vehiclesDto) {
			
			
		}
		
		return response.getBody();
	}
	
	
	
	@RequestMapping(method=RequestMethod.PUT,value = "/vehicles/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
	public void putVehicleById(@PathVariable String id, @RequestBody VehicleDto vehicleDTO) {
		//get vehicle par id  depuis repository
		Vehicle vehicle = vService.getVehicleById(Integer.valueOf(id));
		int idDTO = vehicle.getIdDto();
		String urlSimulator = "http://localhost:8081/vehicle/"+idDTO;
		
		/* id du vehicleDTO doit etre mise a jour*/
		vehicleDTO.setId(idDTO);
		
		/* TODO mettre a jour le repository*/
		
		//mettre a jour dans le FireSimulator
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<VehicleDto> request = new HttpEntity<>(vehicleDTO);
		ResponseEntity<String> response = restTemplate
		  .exchange(urlSimulator, HttpMethod.PUT, request, String.class);
		
		//get response
		String operation = response.getBody();
		System.out.println("operation update:"+operation);
		
	}
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = "/vehicles/{id}")
	public void deleteVehicle(@PathVariable String id ) {
		vService.deleteVehicleById(Integer.valueOf(id));
	}
	
	
	
	private void updateLocalRepositor(String urlSimulator) {
		Vehicle vehicle;
		
		//faire la requete vers FireSimulator pour avoir tous les vehicules 
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<VehicleDto[]> response = restTemplate.exchange(urlSimulator, HttpMethod.GET,null,VehicleDto[].class);
		
		//mettre le repository a jour 
		VehicleDto[] vehiclesDto = response.getBody();
		for(VehicleDto vehicleDto : vehiclesDto) {
			vehicle = vService.getVehicleByIdDto(vehicleDto.getId());
			if(vehicle != null) {
				
				
				}
			else {
				vehicle = new Vehicle(vehicleDto);
				vehicle.setIdDto(vehicleDto.getId());
				vService.addVehicle(vehicle);
				}
			
		
			}
		
		}
	
	
	
	

}
