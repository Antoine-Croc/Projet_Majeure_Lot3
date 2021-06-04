package com.project.service;

import org.springframework.stereotype.Service;

import com.project.repository.VehicleRepository;

@Service
public class RunnableMng {
	private VehicleService vService;

	UpdateRunnable updateRunnable;
	private Thread updateThread;
	
	InterventionRunnable interventionRunnabe;
	private Thread interventionThread;

	public RunnableMng(VehicleService vService) {
		this.vService = vService;


		// Create a Runnable is charge of executing cyclic actions
		this.updateRunnable = new UpdateRunnable(this.vService);

		// A Runnable is held by a Thread which manage lifecycle of the Runnable
		updateThread = new Thread(updateRunnable);

		// The Thread is started and the method run() of the associated DisplayRunnable
		// is launch
		updateThread.start();
		
		this.interventionRunnabe = new InterventionRunnable(this.vService);
		interventionThread = new Thread(interventionRunnabe);
		interventionThread.start();
		
		

	}

}
