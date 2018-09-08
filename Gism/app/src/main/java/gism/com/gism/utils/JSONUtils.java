package gism.com.gism.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Sagar on 4/5/2017.
 */
public class JSONUtils {

    private static JSONUtils instance = new JSONUtils();

    private static ObjectMapper objectMapper = new ObjectMapper();

    private JSONUtils(){}

    public static JSONUtils getInstance(){
        if(instance == null){
            synchronized (instance){
                objectMapper = new ObjectMapper();
            }
        }
        return instance;
    }

    public String toJSONString(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    public Object jsonStringToObject(String jsonString, Class classType) throws IOException {
        return objectMapper.readValue(jsonString, classType);
    }
}
