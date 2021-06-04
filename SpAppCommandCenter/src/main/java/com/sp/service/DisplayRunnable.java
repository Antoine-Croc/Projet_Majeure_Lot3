package com.sp.service;

import com.sp.model.CommandCenter;
import com.sp.repo.CommandCenterRepo;
import com.sp.service.CommandCenterService;
public class DisplayRunnable implements Runnable {
	
	boolean isEnd = false;

	public DisplayRunnable() {}

	@Override
	public void run() {
		while (!this.isEnd) {
			try {
				Thread.sleep(10000);
				CommandCenterService.verificationFeu();
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
