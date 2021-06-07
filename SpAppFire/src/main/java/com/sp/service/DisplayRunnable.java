package com.sp.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.project.model.dto.FireDto;
import com.sp.model.Fire;
import com.sp.repo.FireRepo;
import com.sp.service.FireService;
public class DisplayRunnable implements Runnable {
	
	boolean isEnd = false;
	FireRepo fireRepo;
	public DisplayRunnable() {}

	@Override
	public void run() {
		while (!this.isEnd) {
			try {
				Thread.sleep(10000);
				System.out.println("MAJ Feu existant");
				HttpHeaders headers = new HttpHeaders();
				HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
				new RestTemplate().postForEntity( "http://localhost:8083/fires/update", request , String.class );
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Runnable DisplayRunnable ends.... ");
	}

	public void stop() {
		this.isEnd = true;
	}

}
