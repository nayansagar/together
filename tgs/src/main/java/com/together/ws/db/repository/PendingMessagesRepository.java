package com.together.ws.db.repository;

import com.together.ws.db.entity.PendingMessage;
import com.together.ws.db.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PendingMessagesRepository extends CrudRepository<PendingMessage, Long> {

    List<PendingMessage> findByUser(String userId);
}
