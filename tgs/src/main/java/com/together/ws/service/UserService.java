package com.together.ws.service;

import com.together.ws.db.entity.Family;
import com.together.ws.db.entity.PendingMessage;
import com.together.ws.db.entity.Relationship;
import com.together.ws.db.entity.User;
import com.together.ws.db.repository.FamilyRepository;
import com.together.ws.db.repository.PendingMessagesRepository;
import com.together.ws.db.repository.RelationshipRepository;
import com.together.ws.db.repository.UserRepository;
import com.together.ws.exception.InvalidFamilyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private PendingMessagesRepository pendingMessagesRepository;

    public Map<String, Object> loginUser(String name, String phone){

        List<User> userEntries = userRepository.findByPhoneAndState(phone, "INVITED");

        Map<String, Object> response = new HashMap<String, Object>();
        if(userEntries == null || userEntries.isEmpty()){
            String userId = UUID.randomUUID().toString();
            User user = new User(userId, name, phone, null, "NEW");
            userRepository.save(user);
            response.put("user_id", userId);
        }else {
            response.put("user_id", userEntries.get(0).getUserId());
        }
        response.put("invites", getFamiliesList(userEntries));
        return response;
    }

    private List<Map<String, String>> getFamiliesList(List<User> userEntries) {
        List<Map<String,String>> familiesList = new ArrayList<Map<String, String>>();

        for(User user : userEntries){
            Map<String, String> familyDetails = new HashMap<String, String>();
            familyDetails.put("id", user.getFamily().getFamilyId());
            familyDetails.put("name", user.getFamily().getName());
            familiesList.add(familyDetails);
        }
        return familiesList;
    }

    public void inviteUsersToFamily(String primaryUserId, String familyId, List<Map<String, Object>> membersList){
        User primaryUser = userRepository.findByUserIdAndFamily(primaryUserId, familyId);
        Family family = familyRepository.findByFamilyId(familyId);

        if(primaryUser == null || family == null){
            throw new InvalidFamilyException();
        }
        for(Map<String, Object> memberInfo : membersList){
            String phone = (String) memberInfo.get("phone");
            String name = (String) memberInfo.get("name");
            String relationshipName = (String) memberInfo.get("relationship");
            String userId = UUID.randomUUID().toString();
            User user = new User(userId, name, phone, family, "INVITED");
            userRepository.save(user);

            Relationship relationship = new Relationship(primaryUser, user, relationshipName);
            relationshipRepository.save(relationship);
        }
    }

    public void joinFamily(String userId, String familyId) {
        User user = userRepository.findByUserIdAndFamilyAndState(userId, familyId, "INVITED");

        if(user == null){
            user = userRepository.findByUserIdAndFamilyAndState(userId, null, "NEW");
            if(user == null){
                throw new InvalidFamilyException();
            }
            user.setFamily(familyRepository.findByFamilyId(familyId));
        }
        user.setState("JOINED");
        userRepository.save(user);
    }

    public void updateSession(String userId, String sessionId) {
        List<User> users = userRepository.findByUserId(userId);

        if(users == null || users.isEmpty()){
            throw new InvalidFamilyException();
        }

        for(User user : users){
            user.setSessionId(sessionId);
            userRepository.save(user);
        }

    }

    public List<Map<String, Object>> getPendingMessages(String userId) {
        List<PendingMessage> pendingMessages = pendingMessagesRepository.findByUser(userId);
        List<Map<String, Object>> responseList = new ArrayList<Map<String, Object>>();

        for(PendingMessage pendingMessage : pendingMessages){
            Map<String, Object> message = new HashMap<String, Object>();
            message.put("message", pendingMessage.getMessage());
            message.put("sender", pendingMessage.getSender().getName());
            responseList.add(message);
        }
        return responseList;
    }

    public void removePendingMessages(String userId) {
        pendingMessagesRepository.delete(pendingMessagesRepository.findByUser(userId));
    }
}
