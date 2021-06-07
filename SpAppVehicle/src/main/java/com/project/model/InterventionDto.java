package com.project.model;

public class InterventionDto {
	Vehicle vehicle;
	double fireLat;
	double fireLon;
	public InterventionDto(){}
	public InterventionDto(Vehicle vehicle, double fireLat, double fireLon) {
		super();
		this.vehicle = vehicle;
		this.fireLat = fireLat;
		this.fireLon = fireLon;
	}
	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	public double getFireLat() {
		return fireLat;
	}
	public void setFireLat(double fireLat) {
		this.fireLat = fireLat;
	}
	public double getFireLon() {
		return fireLon;
	}
	public void setFireLon(double fireLon) {
		this.fireLon = fireLon;
	}
	
	

}
