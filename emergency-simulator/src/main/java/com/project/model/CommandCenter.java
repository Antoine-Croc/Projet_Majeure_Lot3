package com.project.model;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CommandCenter {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private ArrayList<Station> stationL;

	public CommandCenter() {
		super();
		this.setStationL(null);
	}
	
	public int getId() {
		return id;
	}
	
	public ArrayList<Station> getStationL() {
		return stationL;
	}

	public void setStationL(ArrayList<Station> stationL) {
		this.stationL = stationL;
	}
	
	
}
