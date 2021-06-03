package com.sp.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.sp.model.Intervention;


public interface InterventionRepo extends CrudRepository<Intervention, Integer> {

	public List<Intervention> findById(int id);
}