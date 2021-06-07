package com.project.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.project.service.RunnableMng;
import com.project.service.VehicleService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Vehicle;
import com.project.model.VehicleIntervention;
import com.project.model.dto.Coord;
import com.project.model.dto.VehicleDto;

@RestController
public class VehicleRestCtrl {
	@Autowired
	VehicleService vService;
	@Autowired
	RunnableMng rService;

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

		// get response( true / false)
		String operation = response.getBody();
		System.out.println("operation update:" + operation);

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
	 * Get un tableau de coordonnées qu'on utilise pour diriger le vehicle
	 * [[45.766994, 4.831062], [45.767246, 4.830966], [45.767002, 4.830155]]
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/route/{latInit}/{lonInit}/{latFinal}/{lonFinal}")
	public List<ArrayList<Float>> getRoute(@PathVariable String lonInit, @PathVariable String latInit,
			@PathVariable String lonFinal, @PathVariable String latFinal) throws JsonParseException, IOException {

		String access_token = "pk.eyJ1IjoidXV1dWlpaWkiLCJhIjoiY2twZjZrYzA4MjM0ODJ5b2dtOHBscmgwNSJ9.k_fmJIDMSqhk58fursNr2A";

		String MapBoxApi = "https://api.mapbox.com/directions/v5/mapbox/driving/" + lonInit + "," + latInit + ";"
				+ lonFinal + "," + latFinal + "?alternatives=true&geometries=geojson&steps=false&access_token="
				+ access_token;
		List<ArrayList<Float>> coords = null;
		List<ArrayList<Float>> listTemp = new ArrayList<ArrayList<Float>>();
		Float temp;
		RestTemplate restTemplate = new RestTemplate();

		try {
			// faire la requete vers MapBoxApi
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(MapBoxApi, String.class);
			String jsonString = responseEntity.getBody();

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

		System.out.println(coords);
		return coords;

	}

	/*
	 * Put mettre a jour la liste qui contient les vehicules en intervention
	 */
//	@CrossOrigin
//	@RequestMapping(method=RequestMethod.POST, value="/vehicles/{idVehicle}", consumes = { MediaType.APPLICATION_JSON_VALUE,
//			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
//					MediaType.APPLICATION_XML_VALUE })
//	@ResponseBody
//	public void getIntervention(@PathVariable("idVehicle") int idVehicle) {
//		System.out.println("Ca fonctionne");
//	}
	
//	@RequestMapping(value="/vehicles/{idVehicle}/coord", method=RequestMethod.POST)
//	public boolean test(@PathVariable int idVehicle) {
//		return true;
//	}
	
	
	/*
	 * Put mettre a jour la liste qui contient les vehicules en intervention
	 */
	@CrossOrigin
	@RequestMapping(value="/vehicles/{idVehicle}/coord", method=RequestMethod.POST)
	public List<ArrayList<Double>> getIntervention(@PathVariable int idVehicle, @RequestParam double lon,@RequestParam double lat) {
		//vService.MAJ(idI,lon,lat);
		VehicleIntervention vehicleIntervention = VehicleIntervention.getInstance();
		System.out.println("Test de cxhzngment d'un vehicule --------------------------");
		Vehicle vehicle = vService.getVehicleById(idVehicle);
		if(vehicle != null) {
			//si le vehicle est deja en intervention
			if(vehicle.isIntervention()) {
				//faire qqc
			}
			
			//sinon on l'ajoute dans la liste d'intervention
			else {
				vehicle.setIntervention(true);
				vehicleIntervention.listIntervention.add(
						new ArrayList<Double>(Arrays.asList((double)idVehicle,lat,lon))
						);
				
				}
		}
		return vehicleIntervention.listIntervention;
		
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET,value="/vehicles/interventions")
	public List<ArrayList<Double>> getListIntervention(){
		VehicleIntervention vehicleIntervention = VehicleIntervention.getInstance();
		return vehicleIntervention.listIntervention;
	}
	
	//TODO verifier pour tous les vehicules en intervention  si ils sont bien à la destination

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
