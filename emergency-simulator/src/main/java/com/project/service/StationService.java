package com.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.project.repo.StationRepo;
import com.project.model.Station;

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
	}
	
	public void removeVehicle(Station station, int idV) {
		station.getVehiclesL().remove(idV);
	}
	
	///TODO integrer class VehicleDto
}
