package com.sp.service;


public class DisplayRunnable implements Runnable {

	private FireService fService;
	boolean isEnd = false;

	public DisplayRunnable(FireService fService) {
		this.fService=fService;
	}

	@Override
	public void run() {
		while (!this.isEnd) {
			try {
				Thread.sleep(10000);
				System.out.println("MAJ Feu existant");
				this.fService.updateFire("http://localhost:8081/fire");
				
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
