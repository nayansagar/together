package com.together.ws.db.entity;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "families", uniqueConstraints = @UniqueConstraint(columnNames = {}))
public class Family {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private BigInteger id;

    @Column
    private String familyId;

    @Column
    private String name;

    @Column
    @OneToMany(mappedBy="family", cascade=CascadeType.ALL)
    private List<User> users;

    public Family(String familyId, String name) {
        this.familyId = familyId;
        this.name = name;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
