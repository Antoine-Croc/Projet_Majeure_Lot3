package com.project.service;

import com.project.model.Vehicle;
import com.project.repository.VehicleRepository;

public class DisplayRunnable implements Runnable {

	private VehicleRepository hrepo;
	boolean isEnd = false;

	public DisplayRunnable(VehicleRepository hrepo) {
		this.hrepo = hrepo;
	}

	@Override
	public void run() {
		while (!this.isEnd) {
			try {
				Thread.sleep(10000);
				//TODO update repository
				
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
