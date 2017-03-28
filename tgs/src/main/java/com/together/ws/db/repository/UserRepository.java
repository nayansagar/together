package com.together.ws.db.repository;

import com.together.ws.db.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByUserId(String userId);

    List<User> findByPhone(String phone);

    List<User> findByFamily(String familyId);

    List<User> findByPhoneAndState(String phone, String state);

    User findByUserIdAndFamily(String userId, String familyId);

    User findByUserIdAndFamilyAndState(String userId, String familyId, String state);
}
