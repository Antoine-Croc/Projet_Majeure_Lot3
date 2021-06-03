package com.sp.model;

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

	public CommandCenter() {}
	
	public int getId() {
		return id;
	}
}