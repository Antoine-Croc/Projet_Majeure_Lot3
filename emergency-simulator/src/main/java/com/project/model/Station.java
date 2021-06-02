package com.project.model;

import java.util.ArrayList;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.project.model.dto.Coord;
import com.project.model.dto.VehicleDto;


@Entity
public class Station {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private Coord coord;
	
	private ArrayList<Integer> vehiclesL;
	
	public Station(Coord coord) {
		this.coord = coord;
		this.vehiclesL = new ArrayList<Integer>();
	}
	public Station(Coord coord, ArrayList<Integer> list) {
		this.coord = coord;
		this.vehiclesL = list; ///potentiellemnt inutile
	}

	@Override
	public String toString() {
		return "Station [id=" + id + " coord=" + coord + ", vehiclesL=" + vehiclesL + "]";
	}
	
	public int getId() {
		return id;
	}

	public Coord getCoord() {
		return coord;
	}

	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	public ArrayList<Integer> getVehiclesL() {
		return vehiclesL;
	}

	public void setVehiclesL(ArrayList<Integer> vehiclesL) {
		this.vehiclesL = vehiclesL;
	}
}
