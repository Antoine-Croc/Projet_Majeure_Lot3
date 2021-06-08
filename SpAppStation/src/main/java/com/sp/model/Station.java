package com.sp.model;

import java.util.ArrayList;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.project.model.dto.Coord;
//import com.project.model.dto.VehicleDto;


@Entity
public class Station {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Embedded
	private Coord coord;
	private int size;
	private int spaceUsed;
	private ArrayList<Integer> vehiclesL;
	
	public Station() {}
	public Station(Coord coord, int size) {
		this.coord = coord;
		this.size = size;
		this.spaceUsed = 0;
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
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSpaceUsed() {
		return spaceUsed;
	}
	
	public void setSpaceUsed(int spaceUsed) {
		this.spaceUsed = spaceUsed;
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
