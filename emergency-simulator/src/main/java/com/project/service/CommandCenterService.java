package com.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.project.model.CommandCenter;
import com.project.model.dto.Coord;
import com.project.model.dto.FireDto;
import com.project.repo.CommandCenterRepo;
public class CommandCenterService {

	@Autowired
	CommandCenterRepo commandCenterRepo;

	public CommandCenter getStation(int id) {
		Optional<CommandCenter> hOpt = CommandCenterRepo.findById(id);
		if (hOpt.isPresent()) {
			return hOpt.get();
		}else {
			return null;
		}
	}
	
	public void verificationFeu() {
		
		ResponseEntity<FireDto[]> resp = new RestTemplate().getForEntity("http://localhost:8081/fire", FireDto[].class);
		FireDto[] fires = resp.getBody();
		ResponseEntity<InterventionDto[]> result = new RestTemplate().getForEntity("http://localhost:8082/interventions", InterventionDto[].class);
		InterventionDto[] interventions = result.getBody();
		
		for (FireDto fire : fires) {
			boolean newFire = true;
			for(InterventionDto intervention : interventions) {
				if(fire.getId() == intervention.getIdFire()) newFire = false;
			}
			if (newFire) {
				Coord casernProche = new Coord(10,10);
				ResponseEntity<StationDto[]> resultat = new RestTemplate().getForEntity("http://localhost:8082/interventions", StationDto[].class);
				StationDto[] stations = resultat.getBody();
				for (StationDto station: stations) {
					if ((Math.abs(casernProche.getLat()- fire.getLat()) > Math.abs(station.getcoordDeLaBase().getLat()- fire.getLat())  || (Math.abs(casernProche.getLon()- fire.getLon()) > Math.abs(station.getcoordDeLaBase().getLon()- fire.getLon()))) {
						casernProche.setLat(station.getcoordDeLaBase().getLat());
						casernProche.setLon(station.getcoordDeLaBase().getLon());
					}
				}
				
			}
		}
	
	}

	public static class InterventionDto{
		int id;
		int idFire;
		
		public InterventionDto() {}
		public int getIdFire() {
			return idFire;
		}
		
		public void setIdFire(int idFire) {
			this.idFire = idFire;
		}
	}
	
	public static class StationDto{
		int id;
		Coord coordDeLaBase;
		
		public StationDto() {}
		
		public Coord getcoordDeLaBase() {
			return coordDeLaBase;
		}
		
		public void setIdFire(Coord coordDeLaBase) {
			this.coordDeLaBase = coordDeLaBase;
		}
	}
}
