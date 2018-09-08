package com.together.ws.http;

import com.together.ws.dto.GroupActivity;
import com.together.ws.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.PathParam;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @RequestMapping(value = "/{groupId}/activities", method = RequestMethod.GET)
    public ResponseEntity getActivities(@PathParam("groupId") String groupId){
        List<GroupActivity> groupActivities = groupService.getGroupActivities(groupId);
        return ResponseEntity.ok(groupActivities);
    }
}
