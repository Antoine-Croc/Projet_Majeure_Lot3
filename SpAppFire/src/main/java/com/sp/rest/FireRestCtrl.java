package com.sp.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.dto.Coord;
import com.sp.model.Fire;
import com.sp.service.FireService;

@RestController
public class FireRestCtrl {

	@Autowired
	FireService fService;
	
	@RequestMapping(method=RequestMethod.POST,value="/fires")
	public void addFire(@RequestBody int id, @RequestBody String type, @RequestBody float intensity, @RequestBody float range, @RequestBody double lon, @RequestBody double lat) {
		Fire f = new Fire(id,type,intensity,range,lon,lat);
		fService.addFire(f);
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/fires/update")
	public void updateFire() {
		fService.updateFire();
	}
	
	
	@RequestMapping(method=RequestMethod.GET,value="/fires/{idF}")
	public Fire getFire(@PathVariable int idF) {
		Fire f = fService.getFire(idF);
		return f;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/fires/{idF}/intensity")
	public float getFireIntensity(@PathVariable int idF) {
		return fService.getIntensity(idF);
	}
	@RequestMapping(method=RequestMethod.GET,value="/fires/intensity")
	public float getFireIntensityByCoord(@RequestParam Coord coord) {
		return fService.getIntensitywithCoord(coord);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/fires")
	public List<Fire> getFires(){
		return fService.getAllFire();
	}
	@RequestMapping(method=RequestMethod.PUT, value="/fires/{idF}")
	public void updateFire(@RequestBody int id, @RequestBody String type, @RequestBody float intensity, @RequestBody float range, @RequestBody double lon, @RequestBody double lat) {
	}
}
