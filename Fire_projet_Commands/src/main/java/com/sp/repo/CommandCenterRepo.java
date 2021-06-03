package com.sp.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.sp.model.CommandCenter;

public interface CommandCenterRepo extends CrudRepository<CommandCenter, Integer> {

	public List<CommandCenter> findById(int id);
}
