package com.together.ws.service;

import com.together.ws.db.entity.Family;
import com.together.ws.db.entity.User;
import com.together.ws.db.repository.FamilyRepository;
import com.together.ws.db.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private UserRepository userRepository;

    public String createFamily(String familyName){
        String familyId = RandomStringUtils.randomAlphabetic(5);
        Family family = new Family(familyId, familyName);
        familyRepository.save(family);
        return familyId;
    }
}
