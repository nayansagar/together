package com.together.ws.utils;

import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionCache {

    private Map<String, Session> sessionCache = new HashMap<String, Session>();

    public Session getSession(String sessionId){
        synchronized (sessionCache){
            return sessionCache.get(sessionId);
        }
    }

    public void addSession(String sessionId, Session session){
        synchronized (sessionCache){
            sessionCache.put(sessionId, session);
        }
    }
}
