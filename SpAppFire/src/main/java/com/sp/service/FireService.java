package com.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.model.dto.Coord;
import com.project.model.dto.FireDto;
import com.sp.model.Fire;
import com.sp.repo.FireRepo;

@Service
public class FireService {

	FireRepo fireRepo;
	DisplayRunnable dRunnable;
	private Thread displayThread;
	
	public FireService(FireRepo hRepo) {
		this.fireRepo = hRepo;
		//Create a Runnable is charge of executing cyclic actions 
		this.dRunnable=new DisplayRunnable();
		
		// A Runnable is held by a Thread which manage lifecycle of the Runnable
		displayThread=new Thread(dRunnable);
		
		// The Thread is started and the method run() of the associated DisplayRunnable is launch
		displayThread.start();
		
	}
	
	public void stopDisplay() {
		//Call the user defined stop method of the runnable
		this.dRunnable.stop();
		try {
			//force the thread to stop
			this.displayThread.join(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

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
	
	public float getIntensitywithCoord(Coord coord) {
		List<Fire> listeAllFire = getAllFire();
		for (int i=0; i < listeAllFire.size(); i++) {
			if (listeAllFire.get(i).getLat() == coord.getLat() && listeAllFire.get(i).getLon() == coord.getLon()) return listeAllFire.get(i).getIntensity(); 
		}
		return 0;
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
