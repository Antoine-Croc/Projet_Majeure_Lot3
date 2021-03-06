package com.project.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.project.service.RunnableMng;
import com.project.service.VehicleService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.InterventionDto;
import com.project.model.Vehicle;
import com.project.model.VehicleIntervention;

import com.project.model.dto.VehicleDto;

@RestController
public class VehicleRestCtrl {
	@Autowired
	VehicleService vService;
	@Autowired
	RunnableMng rService;
	
	/*
	 * Get IsVehicleIntervetion
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/vehicles/{id}/intervention")
	public boolean isVehicleIntervention(@PathVariable String id) {
		Vehicle vehicle = vService.getVehicleById(Integer.valueOf(id));
		return vehicle.isIntervention();
	}
	
	

	/*
	 * Post le nouveau vehicle dans le repository et dans le FireSimulator
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/vehicles", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public Vehicle addVehicle(@RequestBody VehicleDto vehicleDto) {
		Vehicle vehicle = new Vehicle(vehicleDto);
		String urlSimulator = "http://localhost:8081/vehicle";

		// ajouter le vehicule dans le FireSimulator
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<VehicleDto> request = new HttpEntity<>(vehicleDto);
		ResponseEntity<VehicleDto> response = restTemplate.exchange(urlSimulator, HttpMethod.POST, request,
				VehicleDto.class);

		// mettre a jour IdDTO de la classe Vehicle et l'ajouter dans le repository
		VehicleDto responseVehicle = response.getBody();
		vehicle.setIdDto(responseVehicle.getId());
		vService.addVehicle(vehicle);

		if (response.getStatusCode() == HttpStatus.OK) {
			System.out.println("add vehicle : " + vehicle);
		}
		return vehicle;
	}
	/*
	 * R??cup??re l'espace qu'occupe le vehicule.
	 */
	@RequestMapping(method=RequestMethod.GET, value="/vehicles/{idV}/size")
	public int getSize(@PathVariable int idV) {
		Vehicle vehicle = vService.getVehicleById(idV);
		return vehicle.getSize();
	}
	
	/*
	 * Get le vehicle par id de la classe Vehicle
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/vehicles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Vehicle getVehicleById(@PathVariable String id) {
		Vehicle vehicle = vService.getVehicleById(Integer.valueOf(id));
		return vehicle;
	}

	/*
	 * Get tous les vehicules depuis FireSimulator
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Vehicle> getAllVehicle() {

		// synchroniser entre le local repository et le FireSimalator
		String urlSimulator = "http://localhost:8081/vehicle";
		vService.updateLocalRepository(urlSimulator);

		// renvoyer la liste de tous les vehicles
		return vService.getAllVehicles();
	}
	

	/*
	 * Put mettre a jour le vehicle dans le repository et dans le FireSimulator
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/vehicles/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void putVehicleById(@PathVariable String id, @RequestBody VehicleDto vehicleDto) {
		// get vehicle par id depuis repository
		Integer idVehicle = Integer.valueOf(id);
		Vehicle vehicle = vService.getVehicleById(idVehicle);
		Integer idDTO = vehicle.getIdDto();
		String urlSimulator = "http://localhost:8081/vehicle/" + idDTO;

		/* id du vehicleDto doit etre mise a jour */
		vehicleDto.setId(idDTO);

		// mettre a jour dans le FireSimulator
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<VehicleDto> request = new HttpEntity<>(vehicleDto);
		ResponseEntity<String> response = restTemplate.exchange(urlSimulator, HttpMethod.PUT, request, String.class);

		/* mettre a jour le repository */
		// updateLocalRepositoryById(idVehicle, idDTO);
		vehicle = vService.updateAttributes(vehicle, vehicleDto);
		vService.addVehicle(vehicle);
		
		
		//mettre ?? jour la position du vehicle dans la liste d'intervention
		updateListIntervention(vehicle);
		
		
		// get response( true / false)
		String operation = response.getBody();
		System.out.println("operation update:" + operation);
		System.out.println("vehicle: id= " + vehicle.getId() +", position: "+vehicle.getLat() + ":"+vehicle.getLon() );

	}

	/*
	 * DELETE supprimer le vehicle dans le repository et dans le FireSimulator
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.DELETE, value = "/vehicles/{id}")
	public void deleteVehicle(@PathVariable String id) {
		Integer idVehicle = Integer.valueOf(id);
		// supprimer dans le repository
		Vehicle vehicle = vService.getVehicleById(idVehicle);
		vService.deleteVehicleById(idVehicle);

		// supprimer dans le FireSimulator
		String urlSimulator = "http://localhost:8081/vehicle/" + vehicle.getIdDto();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(urlSimulator, HttpMethod.DELETE, null, String.class);

		// get response( true / false)
		String operation = response.getBody();
		System.out.println("operation delete:" + operation);
	}
	
	/*
	 * check fuel
	 */
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET, value = "/vehicles/{id}/route/{latInit}/{lonInit}/{latFinal}/{lonFinal}")
	public boolean isRoutePossibleById(@PathVariable String lonInit, @PathVariable String latInit,
			@PathVariable String lonFinal, @PathVariable String latFinal,@PathVariable String id) throws JsonParseException, IOException {
		boolean result = false;
		float distance = 0;
		Vehicle vehicle = vService.getVehicleById(Integer.valueOf(id));
		try {
			String jsonString = vService.getRoute( lonInit,  latInit, lonFinal, latFinal);

			// convertir le string obtenu dans la classe RouteBean
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree = mapper.readTree(jsonString);

			List<JsonNode>  distanceList = tree.findValues("distance");
			JsonNode jsonDistance = distanceList.get(0);
			 distance = mapper.convertValue(jsonDistance, Float.class);
			 //si le fuel du vehicule est suffisant
 			float calcul =  Math.abs(  vehicle.getFuel() - 2*((float)(distance/1000)*vehicle.getFuelConsumption()) );

			 if(  calcul >= 1e-1 ) {
				 result = true;
				vehicle.setFuel(vehicle.getFuel()-calcul);
				vService.addVehicle(vehicle);
			 }
			 
			
		}catch (RestClientException e) {
			// process exception
			if (e instanceof HttpStatusCodeException) {
				String errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();
				System.out.println("------------------" + errorResponse);

				}
			}
		
		return result;
	}
	

	/*
	 * Get un tableau de coordonn??es qu'on utilise pour diriger le vehicle
	 * [[45.766994, 4.831062], [45.767246, 4.830966], [45.767002, 4.830155]]
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/route/{latInit}/{lonInit}/{latFinal}/{lonFinal}")
	public List<ArrayList<Float>> getRoute(@PathVariable String lonInit, @PathVariable String latInit,
			@PathVariable String lonFinal, @PathVariable String latFinal) throws JsonParseException, IOException {

		List<ArrayList<Float>> coords = null;
		List<ArrayList<Float>> listTemp = new ArrayList<ArrayList<Float>>();
		Float temp;
	

		try {
	
			String jsonString = vService.getRoute( lonInit,  latInit, lonFinal, latFinal);
			// convertir le string obtenu dans la classe RouteBean
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree = mapper.readTree(jsonString);

			List<JsonNode> geometryList = tree.findValues("geometry");
			JsonNode geometry = geometryList.get(0);

			RouteBean routeBean = mapper.convertValue(geometry, RouteBean.class);

			coords = routeBean.getCoordinates();

			// permuter [lon,lat] en [lat,lon]
			for (ArrayList<Float> list : coords) {
				temp = list.get(1);
				list.set(1, list.get(0));
				list.set(0, temp);
				listTemp.add(list);
			}
			coords = listTemp;

		} catch (RestClientException e) {
			// process exception
			if (e instanceof HttpStatusCodeException) {
				String errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();
				System.out.println("------------------" + errorResponse);

			}

		}		
		//verifier si les coordonnees finales correspondent bien a celles de la destination 
		ArrayList<Float> lastCoords = coords.get(coords.size()-1);
		
		if( Math.abs( lastCoords.get(0) - Float.parseFloat(latFinal) ) <=1e-4 
				&& Math.abs( lastCoords.get(1) - Float.parseFloat(lonFinal) ) <=1e-4 ) {
			//on fait qqc
		}
		//sinon on rajoute les coordonnees de la destination dans la liste
		else {
			ArrayList<Float> destinationCoords = new  ArrayList<Float>(Arrays.asList(
					Float.parseFloat(latFinal),Float.parseFloat(lonFinal) 
					 )
					);
			
			coords.add(destinationCoords);
		}
		
		System.out.println(coords);
		return coords;

	}
	
	/*
	 * Post mettre a jour la liste qui contient les vehicules en intervention
	 */
	@CrossOrigin
	@RequestMapping(value="/vehicles/{idVehicle}/coord", method=RequestMethod.POST)
	public List<InterventionDto> getIntervention(@PathVariable int idVehicle, @RequestParam double lon,@RequestParam double lat) {
		//vService.MAJ(idI,lon,lat);
		VehicleIntervention vehicleIntervention = VehicleIntervention.getInstance();
	
		Vehicle vehicle = vService.getVehicleById(idVehicle);
		if(vehicle != null) {
			//si le vehicle est deja en intervention
			if(vehicle.isIntervention()) {
				//faire qqc
			}
			
			//sinon on l'ajoute dans la liste d'intervention
			else {
				vehicle.setIntervention(true);
				//save
				vService.addVehicle(vehicle);
				//System.out.println("-------------------------"+vService.getVehicleById(idVehicle).isIntervention());
				vehicleIntervention.listIntervention.add(new InterventionDto(vehicle,lat,lon));
				}
		}
		return vehicleIntervention.listIntervention;
		
	}
	
	
	/*
	 * GET renvoyer la liste d'interventions 
	 * [ [idVehicle, FireLat,FireLon],[idVehicle, FireLat,FireLon]]
	 */
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET,value="/vehicles/interventions")
	public List<InterventionDto> getListIntervention(){
		VehicleIntervention vehicleIntervention = VehicleIntervention.getInstance();
		return vehicleIntervention.listIntervention;
	}
	


	public static class RouteBean {
		List<ArrayList<Float>> coordinates;
		String type;

		public RouteBean() {
		}

		public List<ArrayList<Float>> getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(List<ArrayList<Float>> l) {
			this.coordinates = l;
		}

		public void setType(String s) {
			this.type = s;
		}

		@Override
		public String toString() {
			return this.coordinates.toString() + this.type;
		}
	}
	
	/*
	 * update la position du vehicule dans la liste intervention
	 */
	private void updateListIntervention(Vehicle vehicle) {
		VehicleIntervention vehicleIntervention = VehicleIntervention.getInstance();
		 
		 List<InterventionDto> listTemp = new ArrayList<InterventionDto>();
		 
		 for(InterventionDto interventionDto : vehicleIntervention.listIntervention) {
			 if(interventionDto.getVehicle().getId() == vehicle.getId()) {
				 interventionDto.setVehicle(vehicle);
			 }
			 listTemp.add(interventionDto);
			 
		 }
		 vehicleIntervention.listIntervention = listTemp;
		 
	}

	/*
	 * 
	 * 
	 * mettre a jour le local repository (tout vehicle) en consultant le
	 * FireSimulator
	 * 
	 * private void updateLocalRepository(String urlSimulator) { Vehicle vehicle;
	 * 
	 * //faire la requete vers FireSimulator pour avoir tous les vehicules
	 * RestTemplate restTemplate = new RestTemplate(); ResponseEntity<VehicleDto[]>
	 * response = restTemplate.exchange(urlSimulator,
	 * HttpMethod.GET,null,VehicleDto[].class);
	 * 
	 * //mettre le repository a jour VehicleDto[] vehiclesDto = response.getBody();
	 * for(VehicleDto vehicleDto : vehiclesDto) {
	 * 
	 * vehicle = vService.getVehicleByIdDto(vehicleDto.getId()); //si le vehicle
	 * existe deja dans le local repository, //mettre a jour ses attributs
	 * if(vehicle != null) { vehicle = updateAttributes(vehicle,vehicleDto);
	 * vService.addVehicle(vehicle); } //sinon on cree un nouveau vehicle dans le
	 * local repository else { vehicle = new Vehicle(vehicleDto);
	 * vehicle.setIdDto(vehicleDto.getId()); vService.addVehicle(vehicle); }
	 * 
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * mettre a jour le local repository (le vehicle avec id ) en consultant le
	 * FireSimulator
	 * 
	 * private void updateLocalRepositoryById(Integer idVehicle, Integer
	 * idVehicleDto) { Vehicle vehicle = vService.getVehicleById(idVehicle); String
	 * urlSimulator = "http://localhost:8081/vehicle/"+idVehicleDto; RestTemplate
	 * restTemplate = new RestTemplate();
	 * 
	 * 
	 * ResponseEntity<VehicleDto> response = restTemplate.exchange(urlSimulator,
	 * HttpMethod.GET,null,VehicleDto.class); VehicleDto vehicleDto =
	 * response.getBody();
	 * 
	 * vehicle = updateAttributes(vehicle,vehicleDto); vService.addVehicle(vehicle);
	 * 
	 * 
	 * }
	 * 
	 * 
	 * mettre a jour tous les attributs d'un vehicle a partir de vehicleDto
	 * 
	 * private Vehicle updateAttributes(Vehicle vehicle, VehicleDto vehicleDto) {
	 * vehicle.setIdDto(vehicleDto.getId()); vehicle.setLon(vehicleDto.getLon());
	 * vehicle.setLat(vehicleDto.getLat()); vehicle.setType(vehicleDto.getType());
	 * vehicle.setEfficiency(vehicleDto.getEfficiency());
	 * vehicle.setLiquidType(vehicleDto.getLiquidType());
	 * vehicle.setLiquidQuantity(vehicleDto.getLiquidQuantity());
	 * vehicle.setLiquidConsumption(vehicleDto.getLiquidConsumption());
	 * vehicle.setFuel( vehicleDto.getFuel()); vehicle.setFuelConsumption(
	 * vehicleDto.getFuelConsumption()); vehicle.setCrewMember(
	 * vehicleDto.getCrewMember()); vehicle.setCrewMemberCapacity(
	 * vehicleDto.getCrewMemberCapacity()); vehicle.setFacilityRefID(
	 * vehicleDto.getFacilityRefID()); return vehicle; }
	 */

}
