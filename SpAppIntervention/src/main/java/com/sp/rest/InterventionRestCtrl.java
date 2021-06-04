package com.sp.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.model.FireStatus;
import com.sp.model.Intervention;
import com.sp.service.InterventionService;

public class InterventionRestCtrl {

	@Autowired
	InterventionService iService;
	
	@RequestMapping(method=RequestMethod.POST,value="/interventions")
	public void addIntervention(@RequestParam int idF) {
		Intervention i = new Intervention(idF);
		iService.addIntervention(i);
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/interventions/{idI}")
	public Intervention getIntervention(@PathVariable int idI) {
		Intervention i = iService.getIntervention(idI);
		return i;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/interventions")
	public List<Intervention> getInterventions(){
		return iService.getAllIntervention();
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/interventions/{idI}/vehicles")
	public List<Integer> getInterventionVehicleList(@PathVariable int idI){
		return iService.getVehicleList(idI);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/interventions/{idI}/vehicles")
	public void addVehicle(@PathVariable int idI,@RequestParam int idV) {
		Intervention i = iService.getIntervention(idI);
		iService.addVehicle(i, idV);
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/interventions/{idI}/vehicles/{idV}")
	public void removeVehicle(@PathVariable int idI,@PathVariable int idV) {
		Intervention i = iService.getIntervention(idI);
		iService.removeVehicle(i, idV);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/interventions/{idI}")
	@GetMapping("/status2str")
	public void modifyStatus(@PathVariable int idI,@RequestParam("status") FireStatus status) {
		Intervention i = iService.getIntervention(idI);
		iService.modifyStatus(i, status);
	}
}
