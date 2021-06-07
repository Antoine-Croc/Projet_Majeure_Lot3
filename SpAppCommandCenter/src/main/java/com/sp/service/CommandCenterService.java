package com.sp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.model.dto.Coord;
import com.project.model.dto.FireDto;
import com.sp.model.CommandCenter;
import com.sp.repo.CommandCenterRepo;

@Service
public class CommandCenterService {

	CommandCenterRepo cRepo;
	DisplayRunnable dRunnable;
	private Thread displayThread;
	
	public CommandCenterService(CommandCenterRepo hRepository) {
		//Replace the @Autowire annotation....
		this.cRepo=hRepository;

		//Create a Runnable is charge of executing cyclic actions 
		this.dRunnable=new DisplayRunnable();
		
		// A Runnable is held by a Thread which manage lifecycle of the Runnable
		displayThread=new Thread(dRunnable);
		
		// The Thread is started and the method run() of the associated DisplayRunnable is launch
		displayThread.start();
		
	}
	
	public void stopDisplay() {
		//Call the user defined stop method of the runnable
		this.dRunnable.stop();
		try {
			//force the thread to stop
			this.displayThread.join(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public CommandCenter getStation(Integer id) {
		Optional<CommandCenter> hOpt = cRepo.findById(id);
		if (hOpt.isPresent()) {
			return hOpt.get();
		}else {
			return null;
		}
	}
	
	public void addCommandCenter() {
		cRepo.save(new CommandCenter());
	}
	
	public static void verificationFeu() {
		
		ResponseEntity<FireDto[]> resp = new RestTemplate().getForEntity("http://localhost:8081/fire", FireDto[].class);
		FireDto[] fires = resp.getBody();
		ResponseEntity<InterventionDto[]> result = new RestTemplate().getForEntity("http://localhost:8086/interventions", InterventionDto[].class);
		InterventionDto[] interventions = result.getBody();
		
		for (FireDto fire : fires) {
			boolean newFire = true;
			boolean ret = false;
			int caserneDejaTest = 0;
			
			for(InterventionDto intervention : interventions) {
				System.out.println(fire.getId()+" =========== "+intervention.getIdFire());
				if(fire.getId() == intervention.getIdFire()) newFire = false;
			}
			while (newFire && !ret) {
				Coord casernProche = new Coord(10,10);
				int idCasernProche = 110;   
				ResponseEntity<StationDto[]> resultat = new RestTemplate().getForEntity("http://localhost:8085/stations", StationDto[].class);
				StationDto[] stations = resultat.getBody();
				System.out.println(stations.toString());
				for (StationDto station: stations) {
					if ((caserneDejaTest != station.getId()) && ((Math.abs(casernProche.getLat()- fire.getLat()) > Math.abs(station.getCoord().getLat()- fire.getLat())  || (Math.abs(casernProche.getLon()- fire.getLon()) > Math.abs(station.getCoord().getLon()- fire.getLon()))))) {
						casernProche.setLat(station.getCoord().getLat());
						casernProche.setLon(station.getCoord().getLon());
						idCasernProche = station.getId();
					}
				}
				//TODO Faire un post 
				String url = "http://localhost:8085/stations/"+idCasernProche;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				url=url+"?idFire="+fire.getId();
				HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
			
System.out.println("idCasernProche = "+idCasernProche);
				ResponseEntity<String> retourStation = new RestTemplate().postForEntity( url, request , String.class );
				System.out.println(retourStation.getBody());
				if(retourStation.getBody().equals("OK")) ret = true;
				else caserneDejaTest = idCasernProche;
			}
		}
	
	}
	
	
	public static class InterventionDto{
		int id;
		int fireId;
		
		public InterventionDto() {}
		public int getIdFire() {
			return fireId;
		}
		
		public void setFireId(int fireId) {
			this.fireId = fireId;
		}
	}
	
	public static class StationDto{
		int id;
		Coord coord;
		
		public StationDto() {}
		
		public Coord getCoord() {
			return coord;
		}
		
		public void setCoord(Coord coord) {
			this.coord = coord;
		}
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		@Override
		public String toString() {
			return "Station : "+id+" --- "+coord;
		}
	}
}
