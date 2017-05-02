package com.together.ws.utils;

import org.springframework.stereotype.Component;

import java.io.*;

/**
 * Created by admin on 5/2/2017.
 */
@Component
public class FileSystemUtils {

    public void write(byte[] content, String fileName) {
        String filePath = EnvironmentUtils.getContentStorePath() + "/" + fileName;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(content);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write file "+fileName);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] read(String fileName){
        String filePath = EnvironmentUtils.getContentStorePath() + "/" + fileName;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            byte[] content = new byte[fileInputStream.available()];
            fileInputStream.read(content);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read file "+fileName);
        }finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(String fileName){
        File file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
    }
}
