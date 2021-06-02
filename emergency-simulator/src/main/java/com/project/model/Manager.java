package com.project.model;

import java.util.ArrayList;

import com.project.model.dto.Coord;
import com.project.model.dto.VehicleDto;

public class Manager {

	private Coord coord;
	
	private ArrayList<VehicleDto> vehiclesL;
	
	public Manager(Coord coord) {
		this.coord = coord;
		this.vehiclesL = null;
	}
	public Manager(Coord coord, ArrayList<VehicleDto> list) {
		this.coord = coord;
		this.vehiclesL = list; ///potentiellemnt inutile
	}

	@Override
	public String toString() {
		return "Manager [coord=" + coord + ", vehiclesL=" + vehiclesL + "]";
	}
	
	public Coord getCoord() {
		return coord;
	}

	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	public ArrayList<VehicleDto> getVehiclesL() {
		return vehiclesL;
	}

	public void setVehiclesL(ArrayList<VehicleDto> vehiclesL) {
		this.vehiclesL = vehiclesL;
	}
}
