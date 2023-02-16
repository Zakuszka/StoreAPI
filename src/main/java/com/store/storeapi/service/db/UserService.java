package com.store.storeapi.service.db;

import com.store.storeapi.pojo.User;

public interface UserService {

	User save(User user);

	User getById(int id);

	User getByUsername(String username);

}
