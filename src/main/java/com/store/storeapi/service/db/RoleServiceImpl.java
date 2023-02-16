package com.store.storeapi.service.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.store.storeapi.dao.RoleRepository;
import com.store.storeapi.pojo.Role;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

	private final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public Role getByName(String role) {
		try {
			return roleRepository.findByName(role);
		} catch (Exception exception) {
			LOGGER.error("Failed to retrieve Role by name: ", exception);
			throw exception;
		}
	}

}
