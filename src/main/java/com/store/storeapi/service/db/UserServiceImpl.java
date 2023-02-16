package com.store.storeapi.service.db;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.store.storeapi.dao.UserRepository;
import com.store.storeapi.pojo.User;
import com.store.storeapi.service.util.OffsetBasedPageRequest;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public User save(User user) {
		try {
			return userRepository.saveAndFlush(user);
		} catch (Exception exception) {
			LOGGER.error("Failed to save the User: ", exception);
			throw exception;
		}
	}

	@Override
	public User getById(int id) {
		try {
			Optional<User> oUser = userRepository.findById(id);

			return oUser.isPresent() ? oUser.get() : null;
		} catch (Exception exception) {
			LOGGER.error("Failed to retrieve User by id: ", exception);
			throw exception;
		}
	}

	@Override
	public User getByUsername(String username) {
		try {
			return userRepository.findByUsername(username);
		} catch (Exception exception) {
			LOGGER.error("Failed to retrieve User by username: ", exception);
			throw exception;
		}
	}

	@Override
	public Page<User> listUsers(Integer limit, Long offset) {
		try {
			return userRepository.findAll(new OffsetBasedPageRequest(offset, limit, Sort.Direction.ASC, "id"));
		} catch (Exception exception) {
			LOGGER.error("Failed to retrieve the list of Users: ", exception);
			throw exception;
		}
	}

}
