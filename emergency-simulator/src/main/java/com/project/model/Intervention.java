package com.project.model;

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
	private Fire fire;
	private String status;
	
	public Intervention(Fire fire) {
		this.vehicleL = new ArrayList<Integer>();;
		this.status = "Ongoing";
		this.fire = fire;
	}

	public int getId() {
		return id;
	}

	public Fire getFire() {
		return fire;
	}

	public void setFire(Fire fire) {
		this.fire = fire;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<Integer> getVehicleL() {
		return vehicleL;
	}

	public void setVehicleL(ArrayList<Integer> vehicleL) {
		this.vehicleL = vehicleL;
	}
}
