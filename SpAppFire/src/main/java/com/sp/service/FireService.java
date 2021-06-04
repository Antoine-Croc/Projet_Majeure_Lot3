package com.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.model.Fire;
import com.sp.repo.FireRepo;

@Service
public class FireService {

	@Autowired
	FireRepo fireRepo;
	
	public void addFire(Fire f) {
		Fire createdFire = fireRepo.save(f);
		System.out.println(createdFire);
	}
	
	public Fire getFire(Integer id) {
		Optional<Fire> hOpt = fireRepo.findById(id);
		if (hOpt.isPresent()) {
			return hOpt.get();
		}else {
			return null;
		}
	}
		
	public List<Fire> getAllFire() {
		List<Fire> fires = new ArrayList<>();
		fireRepo.findAll().forEach(fires::add);
		return fires;
	}
	
	public void modifySize(Fire f,float r) {
		f.setRange(r);
	}
	
	public void modifyIntensity(Fire f, float i) {
		f.setIntensity(i);
	}
}
