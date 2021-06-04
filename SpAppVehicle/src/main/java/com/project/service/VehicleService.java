package com.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.model.Vehicle;
import com.project.model.VehicleIntervention;
import com.project.model.dto.VehicleDto;
import com.project.repository.VehicleRepository;

@Service
public class VehicleService {

	private VehicleRepository vRepository;

	public VehicleService(VehicleRepository vRepository) {
		// Replace the @Autowire annotation....
		this.vRepository = vRepository;
	}

	public void addVehicle(Vehicle vehicle) {
		Vehicle createdVehicle = vRepository.save(vehicle);

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
		System.out.println("Update local repository and FireSimulator");

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
			 for( ArrayList<Double> intervention: vehicleIntervention.listIntervention) {
				  idVehicle = intervention.get(0).intValue();
				  lat = intervention.get(1);
				  lon = intervention.get(2);
				  vehicle = getVehicleById(idVehicle);
				  //si le vehicule arrive a proximite
				  if(Math.abs(vehicle.getLat() - lat)<1e-3 && Math.abs(vehicle.getLon() - lon)<1e-3) {
					  vehicleIntervention.listIntervention.remove(intervention);
					  System.out.println("vehicle:"+idVehicle + "is arrived at "+lat +":"+lon);
				  }
			 	}
		 }
	}

}
