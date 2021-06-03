package com.sp.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

	@RequestMapping(method=RequestMethod.GET,value="/stations")
	public List<Station> getAll() {
		return sService.getAllStation();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/stations/{id}")
	public Station getById(@PathVariable int id) {
		return sService.getStation(id);
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/stations",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public void addStation(@RequestBody Coord coord) {
		Station station = new Station(coord);
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/stations/{id}")
	public String findGoodTruck(@PathVariable int id, @RequestParam int idFire) {
		return sService.findGoodTruck(id, idFire);
	}
}
