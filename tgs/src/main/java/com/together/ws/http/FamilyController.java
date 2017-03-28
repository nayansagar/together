package com.together.ws.http;

import com.together.ws.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/families")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createFamily(@RequestBody Map<String, String> familyData){
        String familyName = familyData.get("name");

        if(StringUtils.isEmpty(familyName)){
            return new ResponseEntity("Missing mandatory field name", HttpStatus.BAD_REQUEST);
        }

        String familyId = familyService.createFamily(familyName);

        return ResponseEntity.created(URI.create(familyId)).build();
    }
}
