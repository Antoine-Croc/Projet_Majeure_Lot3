package com.project.service;

public class InterventionRunnable implements Runnable{


	private VehicleService vService;
	boolean isEnd = false;

	public InterventionRunnable(VehicleService vService) {
		this.vService=vService;
	}

	@Override
	public void run() {
		while (!this.isEnd) {
			try {
				Thread.sleep(5000);
				//verifier la position du vehicule
				this.vService.VehiclePositionIsFinal();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Runnable InterventionRunnable ends.... ");
	}

	public void stop() {
		this.isEnd = true;
	}
	
	
	
	

	


}
