package com.together.ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.ws.db.entity.Family;
import com.together.ws.db.entity.PendingMessage;
import com.together.ws.db.entity.Relationship;
import com.together.ws.db.entity.User;
import com.together.ws.db.repository.FamilyRepository;
import com.together.ws.db.repository.PendingMessagesRepository;
import com.together.ws.db.repository.RelationshipRepository;
import com.together.ws.db.repository.UserRepository;
import com.together.ws.exception.InvalidFamilyException;
import com.together.ws.utils.FileSystemUtils;
import com.together.ws.utils.SessionCache;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.io.IOException;
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

    @Autowired
    private SessionCache sessionCache;

    @Autowired
    private FileSystemUtils fileSystemUtils;

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
            Family family = familyRepository.findByFamilyId(user.getFamily());
            Map<String, String> familyDetails = new HashMap<String, String>();
            familyDetails.put("id", family.getFamilyId());
            familyDetails.put("name", family.getName());
            familiesList.add(familyDetails);
        }
        return familiesList;
    }

    public void inviteUsersToFamily(String primaryUserId, String familyId, List<Map<String, Object>> membersList){

        User primaryUser = userRepository.findByUserIdAndFamilyId(primaryUserId, familyId);

        if(primaryUser == null){
            throw new InvalidFamilyException();
        }
        for(Map<String, Object> memberInfo : membersList){
            String phone = (String) memberInfo.get("phone");
            String name = (String) memberInfo.get("name");
            String relationshipName = (String) memberInfo.get("relationship");
            String userId = UUID.randomUUID().toString();
            User user = new User(userId, name, phone, familyId, "INVITED");
            userRepository.save(user);

            Relationship relationship = new Relationship(primaryUserId, userId, relationshipName);
            relationshipRepository.save(relationship);
        }
    }

    public void joinFamily(String userId, String familyId) {
        User user = userRepository.findByUserIdAndFamilyIdAndState(userId, familyId, "INVITED");

        if(user == null){
            user = userRepository.findByUserIdAndFamilyIdAndState(userId, null, "NEW");
            if(user == null){
                throw new InvalidFamilyException();
            }
            user.setFamily(familyId);
        }
        user.setState("JOINED");
        userRepository.save(user);
    }

    public void updateSession(String userId, Session session) {
        List<User> users = userRepository.findByUserId(userId);

        if(users == null || users.isEmpty()){
            throw new InvalidFamilyException();
        }

        for(User user : users){
            user.setSessionId(session == null ? null : session.getId());
            userRepository.save(user);
        }

        if(session != null)
        sessionCache.addSession(session.getId(), session);

    }

    public List<Map<String, Object>> getPendingMessages(String userId) {
        List<PendingMessage> pendingMessages = pendingMessagesRepository.findByUserId(userId);
        List<Map<String, Object>> responseList = new ArrayList<Map<String, Object>>();

        for(PendingMessage pendingMessage : pendingMessages){
            Map<String, Object> message = new HashMap<String, Object>();
            message.put("message", pendingMessage.getMessage());
            message.put("sender", pendingMessage.getSender());
            responseList.add(message);
        }
        return responseList;
    }

    public void removePendingMessages(String userId) throws IOException {
        List<PendingMessage> pendingMessages = pendingMessagesRepository.findByUserId(userId);
        for(PendingMessage message : pendingMessages){
            deleteFromContentStore(message.getMessage());
        }
        pendingMessagesRepository.delete(pendingMessages);
    }

    public String getUserBySessionId(String sessionId) {
        List<User> users= userRepository.findBySessionId(sessionId);
        if(users != null && !users.isEmpty()){
            return users.get(0).getUserId();
        }
        return null;
    }

    public void handleMessage(Map message, String sessionId) throws IOException {
        String familyId = (String) message.get("family");
        User user = userRepository.findBySessionIdAndFamilyId(sessionId, familyId);
        enhanceMessage(message, user.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        String msgToSend = objectMapper.writeValueAsString(message);
        List<User> familyMembers = userRepository.findByFamilyIdAndState(familyId, "JOINED");
        boolean delete = true;
        for(User member : familyMembers){
            if(member.getUserId().equals(user.getUserId())){
                continue;
            }

            if(!StringUtils.isEmpty(member.getSessionId())){
                Session session = sessionCache.getSession(member.getSessionId());
                if(session == null){
                    addPendingMessage(member, user, msgToSend);
                    delete = false;
                }else {
                    try {
                        session.getBasicRemote().sendText(msgToSend);
                    } catch (IOException e) {
                        addPendingMessage(member, user, msgToSend);
                        delete = false;
                    }
                }
            }else{
                addPendingMessage(member, user, msgToSend);
                delete = false;
            }
        }

        if(delete){
            deleteFromContentStore(message);
        }
    }

    private void deleteFromContentStore(String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map messageMap = objectMapper.readValue(message, Map.class);
        deleteFromContentStore(messageMap);
    }

    private void deleteFromContentStore(Map messageMap) throws IOException {
        String msgType = (String) messageMap.get("type");
        if("internalLink".equals(msgType)){
            String msgBody = (String) messageMap.get("body");
            String fileName = msgBody.substring(msgBody.lastIndexOf("/")+1);
            fileSystemUtils.delete(fileName);
        }
    }

    private void enhanceMessage(Map message, String sender) throws JsonProcessingException {
        message.put("sender", sender);
        message.put("sentAt", DateTime.now().withZone(DateTimeZone.UTC).toString());
    }

    private void addPendingMessage(User member, User sender, String message) {
        PendingMessage pendingMessage = new PendingMessage(member.getUserId(), sender.getUserId(), message);
        pendingMessagesRepository.save(pendingMessage);
    }
}
