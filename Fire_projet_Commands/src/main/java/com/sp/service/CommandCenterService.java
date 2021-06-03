package com.sp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.project.model.dto.Coord;
import com.project.model.dto.FireDto;
import com.sp.model.CommandCenter;
import com.sp.repo.CommandCenterRepo;

public class CommandCenterService {

	@Autowired
	CommandCenterRepo cRepo;

	public CommandCenter getStation(Integer id) {
		Optional<CommandCenter> hOpt = cRepo.findById(id);
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
			boolean ret = false;
			int caserneDejaTest = 0;
			
			for(InterventionDto intervention : interventions) {
				if(fire.getId() == intervention.getIdFire()) newFire = false;
			}
			while (newFire && !ret) {
				Coord casernProche = new Coord(10,10);
				int idCasernProche = 110;   
				ResponseEntity<StationDto[]> resultat = new RestTemplate().getForEntity("http://localhost:8082/stations", StationDto[].class);
				StationDto[] stations = resultat.getBody();
				for (StationDto station: stations) {
					if ((caserneDejaTest != station.getId()) && ((Math.abs(casernProche.getLat()- fire.getLat()) > Math.abs(station.getcoordDeLaBase().getLat()- fire.getLat())  || (Math.abs(casernProche.getLon()- fire.getLon()) > Math.abs(station.getcoordDeLaBase().getLon()- fire.getLon()))))) {
						casernProche.setLat(station.getcoordDeLaBase().getLat());
						casernProche.setLon(station.getcoordDeLaBase().getLon());
						idCasernProche = station.getId();
					}
				}
				//TODO Faire un post 
				String url = "https://localhost:8082/stations/"+idCasernProche;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

				MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
				map.add("idFire", String.valueOf(fire.getId()));

				HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
				ResponseEntity<String> retourStation = new RestTemplate().postForEntity( url, request , String.class );
				
				if(retourStation.equals("OK")) ret = true;
				else caserneDejaTest = idCasernProche;
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
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
	}
}
