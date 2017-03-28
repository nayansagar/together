package com.together.ws.db.repository;

import com.together.ws.db.entity.Family;
import com.together.ws.db.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface FamilyRepository extends CrudRepository<Family, Long> {

    Family findByFamilyId(String familyId);
}
