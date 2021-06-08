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
<<<<<<< HEAD

	public ArrayList<Integer> getFeuAgerer() {
		return feuAgerer;
	}

	public void setFeuAgerer(ArrayList<Integer> feuAgerer) {
		this.feuAgerer = feuAgerer;
	}
	
	
=======
>>>>>>> 1a2f8c966059a1bdfd940c5da56f0d3337e16049
}
