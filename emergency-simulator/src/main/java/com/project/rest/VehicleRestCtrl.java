package com.project.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.project.service.VehicleService;
import com.project.model.dto.VehicleDto;

@RestController
public class VehicleRestCtrl {
	@Autowired
	VehicleService vService;
	
	@RequestMapping(method=RequestMethod.POST,value = "/vehicles",
			consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE)
	public void addVehicle(@RequestBody VehicleDto vehicleDTO ) {
		
		
	}
	
	

}
