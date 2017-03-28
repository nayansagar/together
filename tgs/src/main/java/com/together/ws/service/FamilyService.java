package com.together.ws.service;

import com.together.ws.db.entity.Family;
import com.together.ws.db.repository.FamilyRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    public String createFamily(String familyName){
        String familyId = RandomStringUtils.randomAlphabetic(5);
        Family family = new Family(familyId, familyName);
        familyRepository.save(family);
        return familyId;
    }
}
