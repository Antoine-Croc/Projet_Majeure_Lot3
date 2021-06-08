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
	private List<Integer> feuAgerer = new ArrayList<Integer>();

	public CommandCenter() {}
	
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "C'est beau [id=" + id + "]";
	}

	public List<Integer> getFeuAgerer() {
		return feuAgerer;
	}

	public void setFeuAgerer(List<Integer> feuAgerer) {
		this.feuAgerer = feuAgerer;
	}
	
	
}
