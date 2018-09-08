package com.together.ws.http;

import com.together.ws.exception.InvalidFamilyException;
import com.together.ws.service.GroupService;
import com.together.ws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> userData){
        String name = userData.get("name");
        String phone = userData.get("phone");

        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(phone)){
            return new ResponseEntity<Object>("Mandatory data missing (name/phone)", HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> userInvites;
        try {
            userInvites = userService.loginUser(name, phone);
        }catch (InvalidFamilyException e){
            return new ResponseEntity<Object>("Invalid family ID", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userInvites);
    }

    @RequestMapping(path = "/{user_id}/family", method = RequestMethod.POST)
    public ResponseEntity createFamily(@PathVariable("user_id") String userId, @RequestBody Map<String, Object> familyData){
        String familyName = (String) familyData.get("name");
        List<Map<String, Object>> memberInfo = (List<Map<String, Object>>) familyData.get("members");

        if(StringUtils.isEmpty(familyName)){
            return new ResponseEntity("Missing mandatory field name", HttpStatus.BAD_REQUEST);
        }

        String familyId = groupService.createFamily(familyName);
        userService.joinFamily(userId, familyId);
        userService.inviteUsersToFamily(userId, familyId, memberInfo);

        return ResponseEntity.created(URI.create(familyId)).build();
    }

    @RequestMapping(path = "/{user_id}/families/{family_id}", method = RequestMethod.POST)
    public ResponseEntity joinFamily(@PathVariable("user_id") String userId, @PathVariable("family_id") String familyId){

        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(familyId)){
            return new ResponseEntity("Missing mandatory field name (user_id/family_id)", HttpStatus.BAD_REQUEST);
        }

        userService.joinFamily(userId, familyId);

        return ResponseEntity.ok().build();
    }
}
