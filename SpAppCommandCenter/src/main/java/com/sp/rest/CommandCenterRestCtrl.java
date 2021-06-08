package com.sp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//import org.springframework.web.bind.annotation.RequestParam;
import com.sp.model.CommandCenter;
import com.sp.service.CommandCenterService;


@RestController
public class CommandCenterRestCtrl {

	@Autowired
	private CommandCenterService cService;


	@RequestMapping(method=RequestMethod.GET,value="/commandcenters/{ids}")
	public CommandCenter getStation(@PathVariable int ids) {
		return cService.getStation(ids);
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/commandcenters")
	public void addCommandCenters() {
		cService.addCommandCenter();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/commandcenters")
	public void getStation() {
		cService.verificationFeu();
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/commandcenters/fire/{idFire}")
	public void addFireInCommandCenters(@PathVariable int idFire) {
		cService.addFireEnSuspence(idFire,1);
	}
	
}