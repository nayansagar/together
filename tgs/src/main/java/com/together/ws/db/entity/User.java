package com.together.ws.db.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"phone"}))
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private BigInteger id;

    @Column
    private String userId;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    public User(String userId, String name, String phone, String sessionId, Family family) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.sessionId = sessionId;
        this.family = family;
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

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }
}
