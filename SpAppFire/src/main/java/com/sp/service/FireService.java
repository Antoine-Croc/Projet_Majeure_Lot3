package com.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.model.dto.FireDto;
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
	public float getIntensity(int id) {
		return getFire(id).getIntensity();
	}
	public void updateFire() {
		ResponseEntity<FireDto[]> resp = new RestTemplate().getForEntity("http://localhost:8081/fire", FireDto[].class);
		FireDto[] fires = resp.getBody();
		List<Fire> knownfires = getAllFire();
		for (FireDto fire : fires) {
			boolean known = false;
			for (Fire knownfire : knownfires) {
				if (fire.getId()==knownfire.getId()) known = true;
			}
			if (!known) {
				addFire(new Fire(fire.getId(),fire.getType(),fire.getIntensity(),fire.getRange(),fire.getLon(),fire.getLat()));
			}
		}
	}
}
