package com.project.model;

import java.util.ArrayList;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.project.model.dto.VehicleDto;

public class Intervention {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private ArrayList<VehicleDto> vehicleL;
	private Fire fire;
	private String status;
	
	public Intervention(Fire fire) {
		this.setVehicleL(null);
		this.setStatus("Ongoing");
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

	public ArrayList<VehicleDto> getVehicleL() {
		return vehicleL;
	}

	public void setVehicleL(ArrayList<VehicleDto> vehicleL) {
		this.vehicleL = vehicleL;
	}
}
