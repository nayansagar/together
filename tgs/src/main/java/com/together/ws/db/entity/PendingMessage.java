package com.together.ws.db.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "pending_messages")
public class PendingMessage {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "sender_id")
    private String senderId;

    @Column
    private String message;

    public PendingMessage(){}

    public PendingMessage(String user, String sender, String message) {
        this.userId = user;
        this.senderId = sender;
        this.message = message;
    }

    public String getUser() {
        return userId;
    }

    public void setUser(String user) {
        this.userId = user;
    }

    public String getSender() {
        return senderId;
    }

    public void setSender(String sender) {
        this.senderId = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
