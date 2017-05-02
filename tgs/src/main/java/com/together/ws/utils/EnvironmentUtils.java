package com.together.ws.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by admin on 5/2/2017.
 */
@Component
public class EnvironmentUtils {

    public static String getProperty(String key){
        String value = System.getProperty(key);
        if(value == null){
            value = System.getenv(key);
        }
        return value;
    }

    public static String getContentStorePath() {
        return getProperty("contentStorePath");
    }
}
