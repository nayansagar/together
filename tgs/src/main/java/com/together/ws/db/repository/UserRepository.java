package com.together.ws.db.repository;

import com.together.ws.db.entity.Family;
import com.together.ws.db.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByUserId(String userId);

    List<User> findByPhone(String phone);

    List<User> findByFamilyId(String familyId);

    List<User> findByPhoneAndState(String phone, String state);

    User findByUserIdAndFamilyId(String userId, String familyId);

    User findByUserIdAndFamilyIdAndState(String userId, String familyId, String state);

    List<User> findBySessionId(String sessionId);

    User findBySessionIdAndFamilyId(String sessionId, String familyId);

    List<User> findByFamilyIdAndState(String familyId, String state);
}
