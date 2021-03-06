package com.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.model.dto.FireDto;
import com.project.model.dto.LiquidType;
import com.sp.model.Station;
import com.sp.repo.StationRepo;

import com.project.model.dto.Coord;
import com.project.model.dto.VehicleDto;

@Service
public class StationService {
	@Autowired
	StationRepo stationRepo;
	
	public void addStation(Station s) {
		Station createdStation = stationRepo.save(s);
		System.out.println(createdStation);
	}
	
	public Station getStation(Integer id) {
		Optional<Station> hOpt = stationRepo.findById(id);
		if (hOpt.isPresent()) {
			return hOpt.get();
		}else {
			return null;
		}
	}
	public List<Station> getAllStation(){
		List<Station> stations = new ArrayList<>();
		stationRepo.findAll().forEach(stations::add);
		return stations;
	}
	
	public List<Integer> getStationVehicleList(int idStation){
		List<Station> stations = new ArrayList<>();
		stationRepo.findAll().forEach(stations::add);
		for (Integer i=0;i<stations.size();i++) {
			Station stationTest = stations.get(i);
			if(stationTest.getId() == idStation) {
				return stationTest.getVehiclesL();
			}
		}
		return null;
	}
	
	public void addVehicle(Station station, int idV) {
		ResponseEntity<VehicleDto> vehicleTestTemp= new RestTemplate().getForEntity("http://localhost:8082/vehicles/"+idV, VehicleDto.class);
		VehicleDto vehicleTest = vehicleTestTemp.getBody();
		int vSize = vehicleTest.getType().getSpaceUsedInFacility();
		if (vSize < getStationFreeSpace(station)) {
			station.getVehiclesL().add(idV);
			occupyVehicleSpace(station, idV);
		}
		stationRepo.save(station);
	}
	public void removeVehicle(Station station, int idV) {
		station.getVehiclesL().remove(idV);
		stationRepo.save(station);
	}
	
	public int getStationSize(Station station) {
		return station.getSize();
	}
	
	public void occupyVehicleSpace(Station station, int idV) {
		ResponseEntity<VehicleDto> vehicleTestTemp= new RestTemplate().getForEntity("http://localhost:8082/vehicles/"+idV, VehicleDto.class);
		VehicleDto vehicleTest = vehicleTestTemp.getBody();
		int vSize = vehicleTest.getType().getSpaceUsedInFacility();
		station.setSpaceUsed(station.getSpaceUsed()+vSize);
	}
	
	public int getStationSpaceUsed(Station station) {
		return station.getSpaceUsed();
	}
	
	public int getStationFreeSpace(Station station) {
		return station.getSize()-station.getSpaceUsed(); //TODO
	}
	
	public LiquidType getGoodLiquid(List<LiquidType> allTypes, String fType) {
		float f=0;
		LiquidType GoodLiquid = LiquidType.ALL;
		for (int j=0;j<allTypes.size();j++) {
			System.out.println("efficiency compar??e" + allTypes.get(j).getEfficiency(fType));
			System.out.println("efficiency actuelle" + f);
			if (f<allTypes.get(j).getEfficiency(fType)) {
				f=allTypes.get(j).getEfficiency(fType);
				GoodLiquid=allTypes.get(j);	
			}
		}

		return GoodLiquid;
	}	
	
	public String findGoodTruck(int id, int idFire) {
		String ret = "KO"; 
		List <LiquidType> AllTypes = new ArrayList<LiquidType>();
		AllTypes.add(LiquidType.WATER);
		AllTypes.add(LiquidType.WATER_WITH_ADDITIVES);
		AllTypes.add(LiquidType.CARBON_DIOXIDE);
		AllTypes.add(LiquidType.POWDER);
		Station stationTest = getStation(id);

		ResponseEntity<FireDto> fire= new RestTemplate().getForEntity("http://localhost:8083/fires/"+idFire, FireDto.class);
		for (int i=0;i<4;i++) {	
			System.out.println(AllTypes);
			LiquidType wantedVehicleLiquid = getGoodLiquid(AllTypes, fire.getBody().getType());

			for (Integer idV : stationTest.getVehiclesL()) {
				ResponseEntity<VehicleDto> vehicleTestTemp= new RestTemplate().getForEntity("http://localhost:8082/vehicles/"+idV, VehicleDto.class);
				VehicleDto vehicleTest = vehicleTestTemp.getBody();
				int idVehicule = vehicleTest.getId();
				double lonFeu = fire.getBody().getLon();
				double latFeu = fire.getBody().getLat();
				System.out.println(vehicleTest.toString() + " - -  -------------------- "+ idVehicule);
				if (vehicleTest.getLiquidType().toString().equals(wantedVehicleLiquid.name()) ) {
					ResponseEntity<Boolean> booleeanvehicleG= new RestTemplate().getForEntity("http://localhost:8082/vehicles/"+idV+"/intervention", Boolean.class);
					boolean booleeanvehicle = booleeanvehicleG.getBody();
					System.out.println(booleeanvehicle + " !!!!! ");
					if (!booleeanvehicle) {
						String url = "http://localhost:8086/interventions/"+"?idF="+idFire+"&idV="+idVehicule;
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
						HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
						ResponseEntity<String> retourIntervention = new RestTemplate().postForEntity( url, request , String.class );
						String urlv = "http://localhost:8082/vehicles/"+idVehicule+"/coord?lon="+fire.getBody().getLon()+"&lat="+fire.getBody().getLat();
						ResponseEntity<String> retourVehicule = new RestTemplate().postForEntity( urlv, request , String.class );
						ret = "OK";
						System.out.println("vehicule envoye:----------------------------------- ");
						i=4;
						break;
					}
				}
			}
			AllTypes.remove(wantedVehicleLiquid);
			System.out.println("Retir?? liquide " + wantedVehicleLiquid + " de la liste.");
		}
		return ret; 
	}
}
