package com.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Vehicle;
import com.project.model.VehicleIntervention;
import com.project.model.dto.VehicleDto;
import com.project.repository.VehicleRepository;
<<<<<<< HEAD
import com.project.model.dto.VehicleType;
=======
import com.project.rest.VehicleRestCtrl.RouteBean;
import com.project.model.InterventionDto;
>>>>>>> 0d225c095c47d3af62bb877f01f71fde6e1217cc

@Service
public class VehicleService {

	private VehicleRepository vRepository;

	public VehicleService(VehicleRepository vRepository) {
		// Replace the @Autowire annotation....
		this.vRepository = vRepository;
	}

	public void addVehicle(Vehicle vehicle) {
		vRepository.save(vehicle);
	}

	public int getVehicleSize(Vehicle vehicle) {
		return vehicle.getSize();
	}
	
	public Vehicle getVehicleById(Integer id) {
		Optional<Vehicle> hOpt = vRepository.findById(id);
		if (hOpt.isPresent()) {
			return hOpt.get();
		} else {
			return null;
		}

	}

	public Vehicle getVehicleByIdDto(Integer id) {
		Optional<Vehicle> hOpt = vRepository.findOneByIdDto(id);
		if (hOpt.isPresent()) {
			return hOpt.get();
		} else {
			return null;
		}
	}

	public List<Vehicle> getAllVehicles() {
		List<Vehicle> vehicles = new ArrayList<>();
		vRepository.findAll().forEach(vehicles::add);
		return vehicles;
	}

	public void deleteVehicleById(Integer id) {
		vRepository.deleteById(id);
		System.out.println("delete vehicle id:" + id);
	}

	public void updateLocalRepository(String urlSimulator) {
		Vehicle vehicle;

		// faire la requete vers FireSimulator pour avoir tous les vehicules
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<VehicleDto[]> response = restTemplate.exchange(urlSimulator, HttpMethod.GET, null,
				VehicleDto[].class);

		// mettre le repository a jour
		VehicleDto[] vehiclesDto = response.getBody();
		for (VehicleDto vehicleDto : vehiclesDto) {

			Optional<Vehicle> hOpt = vRepository.findOneByIdDto(vehicleDto.getId());
			if (hOpt.isPresent()) {
				vehicle = hOpt.get();
			} else {
				vehicle=null;
			}

			// si le vehicle existe deja dans le local repository,
			// mettre a jour ses attributs
			if (vehicle != null) {
				vehicle = updateAttributes(vehicle, vehicleDto);
				vRepository.save(vehicle);
			}
			// sinon on cree un nouveau vehicle dans le local repository
			else {
				vehicle = new Vehicle(vehicleDto);
				vehicle.setIdDto(vehicleDto.getId());
				vRepository.save(vehicle);
			}

		}
		

	}

	/*
	 * mettre a jour tous les attributs d'un vehicle a partir de vehicleDto
	 */
	public Vehicle updateAttributes(Vehicle vehicle, VehicleDto vehicleDto) {
		vehicle.setIdDto(vehicleDto.getId());
		vehicle.setLon(vehicleDto.getLon());
		vehicle.setLat(vehicleDto.getLat());
		vehicle.setType(vehicleDto.getType());
		vehicle.setEfficiency(vehicleDto.getEfficiency());
		vehicle.setLiquidType(vehicleDto.getLiquidType());
		vehicle.setLiquidQuantity(vehicleDto.getLiquidQuantity());
		vehicle.setLiquidConsumption(vehicleDto.getLiquidConsumption());
		vehicle.setFuel(vehicleDto.getFuel());
		vehicle.setFuelConsumption(vehicleDto.getFuelConsumption());
		vehicle.setCrewMember(vehicleDto.getCrewMember());
		vehicle.setCrewMemberCapacity(vehicleDto.getCrewMemberCapacity());
		vehicle.setFacilityRefID(vehicleDto.getFacilityRefID());
		return vehicle;
	}
	
	/*
	 * A partir de la liste d'intervention
	 * On verifie si le vehicule arrive a la destination
	 */
	public void VehiclePositionIsFinal() {
		Integer idVehicle ;
		double lat;
		double lon;
		Vehicle vehicle;
		VehicleIntervention vehicleIntervention = VehicleIntervention.getInstance();
		if( ! vehicleIntervention.listIntervention.isEmpty()) {
			//parcourir toutes les interventions
			 for( InterventionDto intervention: vehicleIntervention.listIntervention) {
				  idVehicle = intervention.getVehicle().getId();
				  lat = intervention.getFireLat();
				  lon = intervention.getFireLon();
				  vehicle = getVehicleById(idVehicle);
				  //si le vehicule arrive a proximite et l'intensite du fire est nulle 
				  
				  if( isFireOut(lat,lon) && Math.abs(vehicle.getLat() - lat)<1e-3 
						  && Math.abs(vehicle.getLon() - lon)<1e-3
						  ) {
					  //supprimer le vehicule de la liste
					  vehicleIntervention.listIntervention.remove(intervention);
					  //changer son etat
					  vehicle.setIntervention(false);
					  vRepository.save(vehicle);
					  
					  System.out.println("Fire:" + " at "+lat +":"+lon+" is out thanks to vehicle: "+idVehicle);
				  }
			 	}
		 }
	}
	
	private boolean isFireOut(double lat,double lon) {
		boolean result = false;

		String urlFires = "http://localhost:8083/fires/intensity?lat="+lat+"&lon="+lon;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Float> response = restTemplate.exchange(urlFires, HttpMethod.GET,null,Float.class);
		Float intensity = response.getBody();
		
		if(Math.abs(intensity)<1e-1) {
			result = true;
		}
		
		System.out.println("Fire at "+lat+":"+lon +", intensity: "+intensity);
		return result;
		
	}
	
	/*
	 * Get requete vers API MapBox
	 */
	public String getRoute(String lonInit, String latInit,String lonFinal,String latFinal) {
		String jsonString = null;
		String access_token = "pk.eyJ1IjoidXV1dWlpaWkiLCJhIjoiY2twZjZrYzA4MjM0ODJ5b2dtOHBscmgwNSJ9.k_fmJIDMSqhk58fursNr2A";

		String MapBoxApi = "https://api.mapbox.com/directions/v5/mapbox/driving/" + lonInit + "," + latInit + ";"
				+ lonFinal + "," + latFinal + "?alternatives=true&geometries=geojson&steps=false&access_token="
				+ access_token;
		
		RestTemplate restTemplate = new RestTemplate();

		try {
			// faire la requete vers MapBoxApi
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(MapBoxApi, String.class);
			 jsonString = responseEntity.getBody();


		} catch (RestClientException e) {
			// process exception
			if (e instanceof HttpStatusCodeException) {
				String errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();
				System.out.println("------------------" + errorResponse);

				}

			}
		return jsonString;
		}

}
