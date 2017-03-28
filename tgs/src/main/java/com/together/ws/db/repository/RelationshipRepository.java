package com.together.ws.db.repository;

import com.together.ws.db.entity.Relationship;
import com.together.ws.db.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RelationshipRepository extends CrudRepository<Relationship, Long> {

}
