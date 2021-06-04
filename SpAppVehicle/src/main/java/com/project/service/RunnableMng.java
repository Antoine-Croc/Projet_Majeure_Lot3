package com.project.service;

import org.springframework.stereotype.Service;

import com.project.repository.VehicleRepository;

@Service
public class RunnableMng {
	private VehicleService vService;

	DisplayRunnable dRunnable;
	private Thread displayThread;

	public RunnableMng(VehicleService vService) {
		this.vService = vService;


		// Create a Runnable is charge of executing cyclic actions
		this.dRunnable = new DisplayRunnable(this.vService);

		// A Runnable is held by a Thread which manage lifecycle of the Runnable
		displayThread = new Thread(dRunnable);

		// The Thread is started and the method run() of the associated DisplayRunnable
		// is launch
		displayThread.start();

	}

}
