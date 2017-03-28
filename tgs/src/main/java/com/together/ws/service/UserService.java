package com.together.ws.service;

import com.together.ws.db.entity.Family;
import com.together.ws.db.entity.User;
import com.together.ws.db.repository.FamilyRepository;
import com.together.ws.db.repository.UserRepository;
import com.together.ws.exception.InvalidFamilyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyRepository familyRepository;

    public String createUser(String name, String phone, String familyId){
        String userId = UUID.randomUUID().toString();
        Family family = familyRepository.findByFamilyId(familyId);
        User user = new User(userId, name, phone, null, family);
        userRepository.save(user);
        return userId;
    }
}
