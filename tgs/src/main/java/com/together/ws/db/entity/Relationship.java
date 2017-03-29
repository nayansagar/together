package com.together.ws.db.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "relationships", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id1", "user_id2"}))
public class Relationship {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "user_id1")
    private String user1;

    @Column(name = "user_id2")
    private String user2;

    @Column
    private String relationship;

    public Relationship(){}

    public Relationship(String user1, String user2, String relationship) {
        this.user1 = user1;
        this.user2 = user2;
        this.relationship = relationship;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
