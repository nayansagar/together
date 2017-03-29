package com.together.ws.config;

import com.together.ws.server.SocketServer;
import com.together.ws.service.UserService;
import org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl;
import org.apache.tomcat.jni.Thread;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.websocket.server.ServerEndpointConfig;

@SpringBootApplication(scanBasePackages = {"com.together"})
@EnableWebSocket
public class Application {

    @Autowired
    private UserService userService;

    public static void main(String args[]) {
        if (AuthConfigFactory.getFactory() == null) {
            AuthConfigFactory.setFactory(new AuthConfigFactoryImpl());
        }
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SocketServer socketServer(){
        return new SocketServer(userService);
    }

    @Bean
    public ServerEndpointExporter endpointExporter() {
        return new ServerEndpointExporter();
    }

    @Component
    public static class ServerConfigurator extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

        @Autowired
        private SocketServer socketServer;

        private ApplicationContext applicationContext;

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
}
