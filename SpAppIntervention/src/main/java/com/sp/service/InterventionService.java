package com.sp.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.model.FireStatus;
import com.sp.model.Intervention;
import com.sp.repo.InterventionRepo;

@Service
public class InterventionService {
	@Autowired
	InterventionRepo interventionRepo;
	
	public void addIntervention(Intervention s) {
		Intervention createdIntervention = interventionRepo.save(s);
		System.out.println(createdIntervention);
	}
	
	public Intervention getIntervention(Integer id) {
		Optional<Intervention> hOpt = interventionRepo.findById(id);
		if (hOpt.isPresent()) {
			return hOpt.get();
		}else {
			return null;
		}
	}
	public List<Intervention> getAllIntervention(){
		List<Intervention> interventions = new ArrayList<>();
		interventionRepo.findAll().forEach(interventions::add);
		return interventions;
	}
	
	public List<Integer> getVehicleList(int idIntervention) {
		List<Intervention> interventions = new ArrayList<>();
		interventionRepo.findAll().forEach(interventions::add);
		for (int i=0; i<interventions.size();i++) {
			Intervention interventionTest = interventions.get(i);
			if (interventionTest.getId()== idIntervention) {
				return interventionTest.getVehicleL();
			}
		}
		return null;
	}
	
	public void addVehicle(Intervention intervention, int idV) {
		intervention.getVehicleL().add(idV);
	}
	
	public void removeVehicle(Intervention intervention, int idV) {
		intervention.getVehicleL().remove(idV);
	}
	
	public void modifyStatus(Intervention intervention, FireStatus status) {
		intervention.setFireStatus(status);
	}
}
