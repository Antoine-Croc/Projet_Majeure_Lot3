package com.project.service;


public class UpdateRunnable implements Runnable {

	private VehicleService vService;
	boolean isEnd = false;

	public UpdateRunnable(VehicleService vService) {
		this.vService=vService;
	}

	@Override
	public void run() {
		while (!this.isEnd) {
			try {
				Thread.sleep(10000);
				//synchroniser le local repository et le FireSimulator
				String urlSimulator = "http://localhost:8081/vehicle";
				this.vService.updateLocalRepository(urlSimulator);
			
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Runnable UpdateRunnable ends.... ");
	}

	public void stop() {
		this.isEnd = true;
	}
	
	
	
	

	


}
