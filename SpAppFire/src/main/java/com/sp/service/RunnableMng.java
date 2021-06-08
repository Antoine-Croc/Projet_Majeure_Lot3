package com.sp.service;

import org.springframework.stereotype.Service;


@Service
public class RunnableMng {
	private FireService vService;

	DisplayRunnable updateRunnable;
	private Thread updateThread;
	


	public RunnableMng(FireService vService) {
		this.vService = vService;


		// Create a Runnable is charge of executing cyclic actions
		this.updateRunnable = new DisplayRunnable(this.vService);

		// A Runnable is held by a Thread which manage lifecycle of the Runnable
		updateThread = new Thread(updateRunnable);

		// The Thread is started and the method run() of the associated DisplayRunnable
		// is launch
		updateThread.start();
		
		
		

	}

}
