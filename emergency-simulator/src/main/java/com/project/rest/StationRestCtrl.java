package com.project.rest;

import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.Station;
import com.project.model.dto.Coord;


@RestController
public class StationRestCtrl {

	@RequestMapping("/hello")
	public String sayHello() {
		return "Hello Station !!!";
	}
	@RequestMapping(method=RequestMethod.POST,value="/addStation",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public void addStation(@RequestBody Coord coord) {
		Station station = new Station(coord);
		System.out.println("start-----------"+station);///a modifier
	}
}
