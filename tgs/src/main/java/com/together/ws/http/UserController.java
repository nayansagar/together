package com.together.ws.http;

import com.together.ws.exception.InvalidFamilyException;
import com.together.ws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> userData){
        String name = userData.get("name");
        String phone = userData.get("phone");
        String familyId = userData.get("family");

        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(phone)){
            return new ResponseEntity<Object>("Mandatory data missing (name/phone)", HttpStatus.BAD_REQUEST);
        }
        String userId;
        try {
            userId = userService.createUser(name, phone, familyId);
        }catch (InvalidFamilyException e){
            return new ResponseEntity<Object>("Invalid family ID", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.created(URI.create(userId)).build();
    }

    @RequestMapping(path = "/{user_id}",method = RequestMethod.PATCH)
    public ResponseEntity<?> updateFamily(@PathVariable("user_id") String userId, @RequestBody Map<String, String> userData){
        String familyId = userData.get("family");
        if(StringUtils.isEmpty(familyId)){
            return new ResponseEntity<Object>("Mandatory data missing (family)", HttpStatus.BAD_REQUEST);
        }
        userService.updateFamily(userId, familyId);
        return ResponseEntity.ok().build();
    }
}
