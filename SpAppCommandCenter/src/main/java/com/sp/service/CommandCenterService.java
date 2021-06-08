package com.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.model.dto.Coord;
import com.project.model.dto.FireDto;
import com.project.model.dto.FireType;
import com.project.model.dto.LiquidType;
import com.project.model.dto.VehicleDto;
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
	
	public LiquidType getGoodLiquid(FireType fType) {
		float f=0;
		LiquidType GoodLiquid = LiquidType.ALL;
		List <LiquidType> AllTypes = new ArrayList<LiquidType>();
		AllTypes.add(LiquidType.WATER);
		AllTypes.add(LiquidType.WATER_WITH_ADDITIVES);
		AllTypes.add(LiquidType.CARBON_DIOXIDE);
		AllTypes.add(LiquidType.POWDER);
		for (int j=0;j<AllTypes.size();j++) {
			if (f<AllTypes.get(j).getEfficiency(fType.name())) {
				f=AllTypes.get(j).getEfficiency(fType.name());
				GoodLiquid=AllTypes.get(j);	
			}
		}
		return GoodLiquid;
	}
	
	public LiquidType checkVehicleLiquid(int idV) {
		ResponseEntity<VehicleDto> vehicleTestTemp= new RestTemplate().getForEntity("http://localhost:8082/vehicles/"+idV, VehicleDto.class);
		VehicleDto vehicleTest = vehicleTestTemp.getBody();
		LiquidType VehicleLiquid = vehicleTest.getLiquidType();
		return VehicleLiquid;
	}
	
	public void addCommandCenter() {
		cRepo.save(new CommandCenter());
	}
	
	public static void verificationFeu() {
		try {
			ResponseEntity<StationDto[]> resultat = new RestTemplate().getForEntity("http://localhost:8085/stations", StationDto[].class);
			StationDto[] stations = resultat.getBody();
			if(stations.length > 0) {
				ResponseEntity<FireDto[]> resp = new RestTemplate().getForEntity("http://localhost:8081/fire", FireDto[].class);
				FireDto[] fires = resp.getBody();
				ResponseEntity<InterventionDto[]> result = new RestTemplate().getForEntity("http://localhost:8086/interventions", InterventionDto[].class);
				InterventionDto[] interventions = result.getBody();
				
				for (FireDto fire : fires) {
					boolean newFire = true;
					boolean ret = false;
					List<Integer> caserneDejaTest = new ArrayList<Integer>();
					
					for(InterventionDto intervention : interventions) {
						System.out.println(fire.getId()+" =========== "+intervention.getIdFire());
						if(fire.getId() == intervention.getIdFire()) newFire = false;
					}
					while (newFire && !ret) {
						Coord casernProche = new Coord(10,10);
						int idCasernProche = 110;   
	
						for (StationDto station: stations) {
							if (!(caserneDejaTest.contains(station.getId())) && ((Math.abs(casernProche.getLat()- fire.getLat()) > Math.abs(station.getCoord().getLat()- fire.getLat())  || (Math.abs(casernProche.getLon()- fire.getLon()) > Math.abs(station.getCoord().getLon()- fire.getLon()))))) {
								casernProche.setLat(station.getCoord().getLat());
								casernProche.setLon(station.getCoord().getLon());
								idCasernProche = station.getId();
							}
						}
		
						String url = "http://localhost:8085/stations/"+idCasernProche;
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
						url=url+"?idFire="+fire.getId();
						HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
					
		System.out.println("idCasernProche = "+idCasernProche);
						ResponseEntity<String> retourStation = new RestTemplate().postForEntity( url, request , String.class );
						System.out.println(retourStation.getBody());
						if(retourStation.getBody().equals("OK")) ret = true;
						else caserneDejaTest.add(idCasernProche);
					}
				}
			} else {
				System.out.println("Attention, aucune caserne n'a été créée");
			}
	} catch(Exception e) {
		System.out.println("Surement un manque de service.");
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
