package com.techpulseIt.workflowengine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

@Entity
@Getter
@Setter
@Table(name = "users")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends BaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "user_id"),inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "role_id"))
    private Collection<Role> roles  = new HashSet<>();
}