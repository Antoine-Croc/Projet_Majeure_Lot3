package com.sp.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.POST,value="/fires")
	public void addFire(@RequestBody int id, @RequestBody String type, @RequestBody float intensity, @RequestBody float range, @RequestBody double lon, @RequestBody double lat) {
		Fire f = new Fire(id,type,intensity,range,lon,lat);
		fService.addFire(f);
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.POST,value="/fires/update")
	public void updateFire() {
		fService.updateFire("http://localhost:8081/fire");
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET,value="/fires/{idF}")
	public Fire getFire(@PathVariable int idF) {
		Fire f = fService.getFire(idF);
		return f;
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET,value="/fires/{idF}/intensity")
	public float getFireIntensity(@PathVariable int idF) {
		return fService.getIntensity(idF);
	}
	
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET,value="/fires/intensity")
	public float getFireIntensityByCoord(@RequestParam String lat,@RequestParam String lon) {		Coord coord = new Coord(Double.parseDouble(lon), Double.parseDouble(lat) );
		return fService.getIntensitywithCoord(coord);
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET, value="/fires")
	public List<Fire> getFires(){
		return fService.getAllFire();
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.POST, value="/fires/{idF}/traite")
	public void setTraitFire(@RequestBody int id, @RequestParam boolean traite) {
		fService.setstatuswithId(id, traite);
		
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.POST, value="/fires/{idF}/traite")
	public boolean getTraitFire(@RequestBody int id) {
		return fService.getstatuswithId(id);
		
	}
}
