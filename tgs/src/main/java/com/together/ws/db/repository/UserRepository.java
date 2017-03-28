package com.together.ws.db.repository;

import com.together.ws.db.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUserId(String userId);
}
