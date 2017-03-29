package com.together.ws.db.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"phone", "family_id"}))
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "user_id")
    private String userId;

    @Column
    private String name;

    @Column
    private String phone;

    @Column(name = "session_id")
    private String sessionId;

    @Column
    private String state;

    @Column(name = "family_id")
    private String familyId;

    public User(){}

    public User(String userId, String name, String phone, String familyId, String state) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.familyId = familyId;
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFamily() {
        return familyId;
    }

    public void setFamily(String family) {
        this.familyId = family;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
