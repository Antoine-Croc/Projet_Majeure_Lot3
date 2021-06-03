package com.project.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.project.model.CommandCenter;

public interface CommandCenterRepo extends CrudRepository<CommandCenter, Integer> {

	public List<CommandCenter> findById(int id);
}
