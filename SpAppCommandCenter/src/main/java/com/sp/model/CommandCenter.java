package com.sp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



@Entity
public class CommandCenter {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private ArrayList<Integer> feuAgerer;

	public  CommandCenter() {
		this.feuAgerer  = new ArrayList<Integer>();
	}
	
	
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "C'est beau [id=" + id + "]";
	}

	public ArrayList<Integer> getFeuAgerer() {
		return feuAgerer;
	}

	public void setFeuAgerer(ArrayList<Integer> feuAgerer) {
		this.feuAgerer = feuAgerer;
	}
	
	
}
