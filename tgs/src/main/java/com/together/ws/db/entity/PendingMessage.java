package com.together.ws.db.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "pending_messages")
public class PendingMessage {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private BigInteger id;

    @Column
    private User user;

    @Column
    private User sender;

    @Column
    private String message;

    public PendingMessage(User user, User sender, String message) {
        this.user = user;
        this.sender = sender;
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
