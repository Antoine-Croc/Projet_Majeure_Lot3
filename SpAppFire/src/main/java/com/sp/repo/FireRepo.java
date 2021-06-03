package com.sp.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.sp.model.Fire;

public interface FireRepo extends CrudRepository<Fire, Integer> {

	public List<Fire> findById(int id);
}