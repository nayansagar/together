package com.together.ws.server;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpointConfig;

@Component
public class ServerConfigurator extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

    @Autowired
    private SocketServer socketServer;

    private static ApplicationContext applicationContext;

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        if(socketServer == null){
            while (applicationContext == null){
                try {
                    java.lang.Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
            socketServer = (SocketServer) applicationContext.getBean("socketServer");
        }
        return (T)socketServer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
