package com.sp.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.sp.model.Station;

public interface StationRepo extends CrudRepository<Station, Integer> {

	public List<Station> findById(int id);
	
}