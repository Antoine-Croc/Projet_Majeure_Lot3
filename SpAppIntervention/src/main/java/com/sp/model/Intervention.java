package com.sp.model;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Intervention {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private ArrayList<Integer> vehicleL;
	private int fireId;
	private FireStatus status;
	
	public Intervention(int fireId) {
		this.vehicleL = new ArrayList<Integer>();;
		this.status = FireStatus.New;
		this.fireId = fireId;
	}

	public int getId() {
		return id;
	}

	public int getFireId() {
		return fireId;
	}

	public void setFireId(int fireId) {
		this.fireId = fireId;
	}

	public FireStatus getFireStatus() {
		return status;
	}

	public void setFireStatus(FireStatus status) {
		this.status = status;
	}

	public ArrayList<Integer> getVehicleL() {
		return vehicleL;
	}

	public void setVehicleL(ArrayList<Integer> vehicleL) {
		this.vehicleL = vehicleL;
	}
}