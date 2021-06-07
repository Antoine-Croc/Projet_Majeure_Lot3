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
		station.getVehiclesL().add(idV);
		stationRepo.save(station);
	}
	
	public void removeVehicle(Station station, int idV) {
		station.getVehiclesL().remove(idV);
		stationRepo.save(station);
	}
	
	public String findGoodTruck(int id, int idFire) {
		String ret = "KO"; 
		Station stationTest = getStation(id);
		System.out.println(id);
		ResponseEntity<FireDto> fire= new RestTemplate().getForEntity("http://localhost:8083/fires/"+idFire, FireDto.class);
		for (Integer idV : stationTest.getVehiclesL()) {
			ResponseEntity<VehicleDto> vehicleTestTemp= new RestTemplate().getForEntity("http://localhost:8082/vehicles/"+idV, VehicleDto.class);
			VehicleDto vehicleTest = vehicleTestTemp.getBody();
			int idVehicule = vehicleTest.getId();
			System.out.println(vehicleTest.toString() + " - -  -------------------- "+ idVehicule);
			if (vehicleTest.getLiquidType().toString().equals("ALL") ) {
				String url = "http://localhost:8086/interventions/"+"?idF="+idFire+"&idV="+idVehicule;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
				ResponseEntity<String> retourIntervention = new RestTemplate().postForEntity( url, request , String.class );
				System.out.println("- -------------------------------------------- http://localhost:8082/vehicles/"+idVehicule+"/coord?lon="+fire.getBody().getLon()+"&lat="+fire.getBody().getLat());
				String urlv = "http://localhost:8082/vehicles/"+idVehicule+"/coord?lon="+fire.getBody().getLon()+"&lat="+fire.getBody().getLat();
				ResponseEntity<String> retourVehicule = new RestTemplate().postForEntity( urlv, request , String.class );
				ret = "OK";
				break;
			}
		}
		return ret; 
	}
}
