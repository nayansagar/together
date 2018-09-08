package com.together.ws.service;

import com.together.ws.db.entity.Family;
import com.together.ws.db.entity.User;
import com.together.ws.db.repository.FamilyRepository;
import com.together.ws.db.repository.UserRepository;
import com.together.ws.dto.GroupActivity;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GroupService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private UserRepository userRepository;

    public String createFamily(String familyName){
        String familyId = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        Family family = new Family(familyId, familyName);
        familyRepository.save(family);
        return familyId;
    }

    public List<GroupActivity> getGroupActivities(String groupId) {
        List<GroupActivity> groupActivities = new ArrayList<>();
        groupActivities.add(new GroupActivity("1", "Shopping List 1", "http://192.168.56.1:8080/activities/act-1.html"));
        groupActivities.add(new GroupActivity("2", "Shopping List 2", "http://192.168.56.1:8080/activities/act-2.html"));
        groupActivities.add(new GroupActivity("3", "Shopping List 3", "http://192.168.56.1:8080/activities/act-3.html"));
        groupActivities.add(new GroupActivity("4", "Shopping List 4", "http://192.168.56.1:8080/activities/act-4.html"));
        groupActivities.add(new GroupActivity("5", "Shopping List 5", "http://192.168.56.1:8080/activities/act-5.html"));
        groupActivities.add(new GroupActivity("6", "Shopping List 6", "http://192.168.56.1:8080/activities/act-6.html"));
        groupActivities.add(new GroupActivity("7", "Shopping List 7", "http://192.168.56.1:8080/activities/act-7.html"));
        groupActivities.add(new GroupActivity("8", "Shopping List 8", "http://192.168.56.1:8080/activities/act-8.html"));
        return groupActivities;
    }
}
