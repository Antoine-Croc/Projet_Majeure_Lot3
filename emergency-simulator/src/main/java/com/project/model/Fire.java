package com.project.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.project.model.dto.Coord;
import com.project.model.dto.FireType;

public class Fire {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private Coord coord;
	private FireType type;
	private int fireRadius;
	
	public Fire(FireType type,Coord coord, int fireRadius) {
		this.type=type;
		this.coord=coord;
		this.setFireRadius(fireRadius);
	}

	public int getId() {
		return id;
	}

	public Coord getCoord() {
		return coord;
	}

	public FireType getType() {
		return type;
	}

	public void setType(FireType type) {
		this.type = type;
	}

	public int getFireRadius() {
		return fireRadius;
	}

	public void setFireRadius(int fireRadius) {
		this.fireRadius = fireRadius;
	}
}
