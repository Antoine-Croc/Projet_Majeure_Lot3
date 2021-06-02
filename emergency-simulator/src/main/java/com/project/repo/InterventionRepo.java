package com.project.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.project.model.Intervention;


public interface InterventionRepo extends CrudRepository<Intervention, Integer> {

	public List<Intervention> findById(int id);
}