package com.project.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.project.model.Station;


public interface StationRepo extends CrudRepository<Station, Integer> {

	public List<Station> findById(int id);
	
}