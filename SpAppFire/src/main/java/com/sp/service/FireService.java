package com.sp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.model.dto.Coord;
import com.project.model.dto.FireDto;
import com.sp.model.Fire;
import com.sp.repo.FireRepo;

@Service
public class FireService {
	private FireRepo fireRepo;
	
	DisplayRunnable dRunnable;
	private Thread displayThread;
	
	public FireService(FireRepo hRepo) {
		this.fireRepo = hRepo;
		
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
			if (listeAllFire.get(i).getLat() == coord.getLat() && listeAllFire.get(i).getLon() == coord.getLon()) {
				int id = listeAllFire.get(i).getId(); 
				ResponseEntity<FireDto[]> resp = new RestTemplate().getForEntity("http://localhost:8081/fire", FireDto[].class);
				FireDto[] fires = resp.getBody();
				for (FireDto fire : fires) {
					if (fire.getId() == id) {
						fireRepo.save(new Fire(fire.getId(),fire.getType(),fire.getIntensity(),fire.getRange(),fire.getLon(),fire.getLat()));
						return fire.getIntensity();
					}
				}
			}
		}
		return 0;
	}
	
	public boolean getstatuswithId(int id) {
		List<Fire> listeAllFire = getAllFire();
		for (int i=0; i < listeAllFire.size(); i++) {
			if (listeAllFire.get(i).getId() == id) return listeAllFire.get(i).isTraite();
		}
		return false;
	}
	
	public void setstatuswithId(int id, boolean traite) {
		List<Fire> listeAllFire = getAllFire();
		for (int i=0; i < listeAllFire.size(); i++) {
			if (listeAllFire.get(i).getId() == id) {
				Fire fireAmodif = listeAllFire.get(i);
				fireAmodif.setTraite(traite);
				fireRepo.save(fireAmodif);
			}
		}
	}
	public Fire updateAttributes(Fire fire, FireDto fDTO) {
		fire.setIntensity(fDTO.getIntensity());
		fire.setType(fDTO.getType());
		fire.setRange(fDTO.getRange());
		fire.setLat(fDTO.getLat());
		fire.setLon(fDTO.getLon());
		return fire;
	}
	
	public void updateFire(String urlSimulator) {
		Fire fire;

		// faire la requete vers FireSimulator pour avoir tous les Fires
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<FireDto[]> response = restTemplate.exchange(urlSimulator, HttpMethod.GET, null,
				FireDto[].class);

		// mettre le repository a jour
		FireDto[] FiresDto = response.getBody();
		for (FireDto fireDto : FiresDto) {

			Optional<Fire> hOpt = fireRepo.findOneByIdDto(fireDto.getId());
			if (hOpt.isPresent()) {
				fire = hOpt.get();
			} else {
				fire=null;
			}

			// si le vehicle existe deja dans le local repository,
			// mettre a jour ses attributs
			if (fire != null) {
				fire = updateAttributes(fire, fireDto);
				fireRepo.save(fire);
			}
			// sinon on cree un nouveau vehicle dans le local repository
			else {
				fire = new Fire(fireDto.getId(),fireDto.getType(),fireDto.getIntensity(),fireDto.getRange(),fireDto.getLon(),fireDto.getLat());
				
				fireRepo.save(fire);
			}

		}
	}
}
