package com.together.ws.db.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "relationships", uniqueConstraints = @UniqueConstraint(columnNames = {"user1", "user2"}))
public class Relationship {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private BigInteger id;

    @Column
    private User user1;

    @Column
    private User user2;

    @Column
    private String relationship;

    public Relationship(User user1, User user2, String relationship) {
        this.user1 = user1;
        this.user2 = user2;
        this.relationship = relationship;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
