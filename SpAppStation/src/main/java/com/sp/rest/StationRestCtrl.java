package com.sp.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.dto.Coord;
import com.sp.model.Station;
import com.sp.service.StationService;

@RestController
public class StationRestCtrl {

	@Autowired
	private StationService sService;
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET,value="/stations/{idS}")
	public Station getStation(@PathVariable int idS) {
		Station s = sService.getStation(idS);
		return s;
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET, value="/stations")
	public List<Station> getAllStation(){
		return sService.getAllStation();
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.POST,value="/stations",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public void addStation(@RequestBody Coord coord) {
		Station s = new Station(coord);
		sService.addStation(s);
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.POST,value="/stations/{idS}")
	public String findGoodTruck(@PathVariable int idS, @RequestParam int idFire) {
		System.out.println(idS+" ------------------ "+idFire);
		return sService.findGoodTruck(idS, idFire);
	}
	
	// Faire un put 
	@CrossOrigin
	@RequestMapping(method=RequestMethod.POST,value="/stations/{idS}/vehicles")
	public void addVehicleId(@RequestParam int idV, @PathVariable int idS) {
		Station s = sService.getStation(idS);
		sService.addVehicle(s, idV);
		
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.DELETE,value="/stations/{idS}/vehicles/{idV}")
	public void removeVehicleId(@PathVariable int idS, @PathVariable int idV) {
		Station s = sService.getStation(idS);
		sService.removeVehicle(s, idV);
	}
	

}