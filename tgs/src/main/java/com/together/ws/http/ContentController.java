package com.together.ws.http;

import com.together.ws.dto.Content;
import com.together.ws.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by admin on 5/2/2017.
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createContent(@RequestBody byte[] content, @RequestHeader("Content-Type") String contentType){
        if(content == null || content.length == 0 || StringUtils.isEmpty(contentType)){
            return ResponseEntity.badRequest().build();
        }
        String location = contentService.createContent(content, contentType);
        return ResponseEntity.created(URI.create("/content/"+location)).build();
    }

    @RequestMapping(path = "/{contentId}", method = RequestMethod.GET)
    public ResponseEntity<?> getContent(@PathParam("contentId") String contentId){
        Content content = contentService.getContent(contentId);
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(content.getType()));
        return new ResponseEntity<Object>(content.getBody(), headers, HttpStatus.OK);
    }
}
