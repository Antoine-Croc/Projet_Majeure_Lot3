package com.project.rest;

import java.util.List;

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
	
	/*
	 * Post le nouveau vehicle dans le repository et dans le FireSimulator
	 */
	@RequestMapping(method=RequestMethod.POST,value = "/vehicles",consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public Vehicle addVehicle(@RequestBody VehicleDto vehicleDto ) {
		Vehicle vehicle = new Vehicle(vehicleDto);
		String urlSimulator = "http://localhost:8081/vehicle";
		
		//ajouter le vehicule dans le FireSimulator
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<VehicleDto> request = new HttpEntity<>(vehicleDto);
		ResponseEntity<VehicleDto> response = restTemplate
		  .exchange(urlSimulator, HttpMethod.POST, request, VehicleDto.class);
		 
		//mettre a jour IdDTO de la classe Vehicle et l'ajouter dans le repository
		VehicleDto responseVehicle = response.getBody();
		vehicle.setIdDto(responseVehicle.getId());
		vService.addVehicle(vehicle);
	
	
		  if(response.getStatusCode() == HttpStatus.OK){
				System.out.println("add vehicle : "+ responseVehicle);
	        }
		return vehicle;
	}
	
	/*
	 * Get le vehicle par id de la classe Vehicle
	 */
	@RequestMapping(method=RequestMethod.GET, value = "/vehicles/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
	public Vehicle getVehicleById(@PathVariable String id) {
		Vehicle vehicle = vService.getVehicleById(Integer.valueOf(id));
		return vehicle;
	}
	
	
	/*
	 * Get tous les vehicules depuis FireSimulator
	 */
	@RequestMapping(method=RequestMethod.GET, value = "/vehicles",produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Vehicle>  getAllVehicle() {
		
		//mettre a jour le local repository
		String urlSimulator = "http://localhost:8081/vehicle";
		updateLocalRepository(urlSimulator);
		
		//renvoyer la liste de tous les vehicles
		return  vService.getAllVehicles();
	}
	
	
	/*
	 * Put mettre a jour le vehicle dans le repository et dans le FireSimulator
	 */
	@RequestMapping(method=RequestMethod.PUT,value = "/vehicles/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
	public void putVehicleById(@PathVariable String id, @RequestBody VehicleDto vehicleDto) {
		//get vehicle par id  depuis repository
		Integer idVehicle = Integer.valueOf(id);
		Vehicle vehicle = vService.getVehicleById(idVehicle);
		Integer idDTO = vehicle.getIdDto();
		String urlSimulator = "http://localhost:8081/vehicle/"+idDTO;
		
		/* id du vehicleDto doit etre mise a jour*/
		vehicleDto.setId(idDTO);
		
		//mettre a jour dans le FireSimulator
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<VehicleDto> request = new HttpEntity<>(vehicleDto);
		ResponseEntity<String> response = restTemplate
		  .exchange(urlSimulator, HttpMethod.PUT, request, String.class);
		
		
		/*  mettre a jour le repository*/	
		updateLocalRepositoryById(idVehicle, idDTO);
		
		
		//get response( true / false)
		String operation = response.getBody();
		System.out.println("operation update:"+operation);
		
	}
	
	
	/*
	 * DELETE supprimer le vehicle dans le repository et dans le FireSimulator
	 */
	@RequestMapping(method=RequestMethod.DELETE, value = "/vehicles/{id}")
	public void deleteVehicle(@PathVariable String id ) {
		Integer idVehicle = Integer.valueOf(id);
		//supprimer dans le repository 
		Vehicle vehicle = vService.getVehicleById(idVehicle);
		vService.deleteVehicleById(idVehicle);
		
		//supprimer dans le FireSimulator
		String urlSimulator = "http://localhost:8081/vehicle/"+vehicle.getIdDto();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(urlSimulator, HttpMethod.DELETE,null,String.class);
		
		//get response( true / false)
		String operation = response.getBody();
		System.out.println("operation delete:"+operation);
	}
	
	
	
	
	/*
	 * mettre a jour le local repository (tout vehicle) en consultant le FireSimulator
	 */
	private void updateLocalRepository(String urlSimulator) {
		Vehicle vehicle;
		
		//faire la requete vers FireSimulator pour avoir tous les vehicules 
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<VehicleDto[]> response = restTemplate.exchange(urlSimulator, HttpMethod.GET,null,VehicleDto[].class);
		
		//mettre le repository a jour 
		VehicleDto[] vehiclesDto = response.getBody();
		for(VehicleDto vehicleDto : vehiclesDto) {
			
			vehicle = vService.getVehicleByIdDto(vehicleDto.getId());
			//si le vehicle existe deja dans le local repository,
			//mettre a jour ses attributs
			if(vehicle != null) {
				vehicle = updateAttributes(vehicle,vehicleDto);
				vService.addVehicle(vehicle);
				}
			//sinon on cree un nouveau vehicle dans le local repository
			else {
				vehicle = new Vehicle(vehicleDto);
				vehicle.setIdDto(vehicleDto.getId());
				vService.addVehicle(vehicle);
				}
			
		
			}
		
		}
	
	/*
	 * mettre a jour le local repository (le vehicle avec id ) en consultant le FireSimulator
	 */
	private void updateLocalRepositoryById(Integer idVehicle, Integer idVehicleDto) {
		Vehicle vehicle = vService.getVehicleById(idVehicle);
		String urlSimulator = "http://localhost:8081/vehicle/"+idVehicleDto;
		RestTemplate restTemplate = new RestTemplate();
		

		ResponseEntity<VehicleDto> response = restTemplate.exchange(urlSimulator, HttpMethod.GET,null,VehicleDto.class);
		VehicleDto vehicleDto = response.getBody();
		
		vehicle = updateAttributes(vehicle,vehicleDto);
		vService.addVehicle(vehicle);
		

	}
	
	/*
	 * mettre a jour tous les attributs d'un vehicle a partir de vehicleDto
	 */
	private Vehicle updateAttributes(Vehicle vehicle, VehicleDto vehicleDto) {
		vehicle.setIdDto(vehicleDto.getId());
		vehicle.setLon(vehicleDto.getLon());
		vehicle.setLat(vehicleDto.getLat());
		vehicle.setType(vehicleDto.getType());  
		vehicle.setEfficiency(vehicleDto.getEfficiency()); 
		vehicle.setLiquidType(vehicleDto.getLiquidType());  
		vehicle.setLiquidQuantity(vehicleDto.getLiquidQuantity()); 
		vehicle.setLiquidConsumption(vehicleDto.getLiquidConsumption()); 
		vehicle.setFuel( vehicleDto.getFuel());
		vehicle.setFuelConsumption( vehicleDto.getFuelConsumption());
		vehicle.setCrewMember( vehicleDto.getCrewMember()); 
		vehicle.setCrewMemberCapacity( vehicleDto.getCrewMemberCapacity());
		vehicle.setFacilityRefID( vehicleDto.getFacilityRefID()); 
		return vehicle;
	}
	
	
	
	
	

}
