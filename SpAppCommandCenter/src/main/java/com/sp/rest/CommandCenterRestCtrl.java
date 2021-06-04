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

	@RequestMapping(method=RequestMethod.GET,value="/CommandCenters/{idS}")
	public CommandCenter getStation(@PathVariable int idS) {
		return cService.getStation(idS);
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/CommandCenters")
	public void addCommandCenters() {
		cService.addCommandCenter();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/CommandCenters")
	public void getStation() {
		cService.verificationFeu();
	}
}