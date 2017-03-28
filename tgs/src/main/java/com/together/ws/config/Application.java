package com.together.ws.config;

import com.together.ws.server.SocketServer;
import com.together.ws.service.UserService;
import org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.security.auth.message.config.AuthConfigFactory;

@SpringBootApplication
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

}
