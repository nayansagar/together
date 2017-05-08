package com.together.ws.service;

import com.together.ws.dto.Content;
import com.together.ws.utils.FileSystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by admin on 5/2/2017.
 */
@Service
public class ContentService {

    @Autowired
    private FileSystemUtils fileSystemUtils;

    public String createContent(byte[] content, String contentType) {
        String contentId = UUID.randomUUID().toString();
        String extension = contentType.split("/")[1];
        String fileName = contentId + "." + extension;
        //calling getType to check if we support the MIME type
        getType(fileName);
        fileSystemUtils.write(content, fileName);
        return fileName;
    }

    public Content getContent(String contentId) {
        Content content = new Content();
        content.setBody(fileSystemUtils.read(contentId));
        content.setType(getType(contentId));
        return content;
    }

    private String getType(String contentId) {
        String extension = contentId.split("\\.")[1];
        switch (extension){
            case "jpg" : return "image/jpg";
            case "jpeg" : return "image/jpg";
            case "png" : return "image/png";
            case "gif" : return "image/gif";
            case "mp4" : return "video/mp4";
            case "mp3" : return "video/mpeg";
            case "mpeg" : return "video/mpeg";
            default: throw new RuntimeException("unsupported file type");
        }
    }
}
