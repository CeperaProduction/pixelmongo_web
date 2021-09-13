package ru.pixelmongo.pixelmongo.model.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "user_groups")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(mappedBy = "group")
    private Set<User> users = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_groups_permissions",
            joinColumns = @JoinColumn(name="group_id"),
            inverseJoinColumns = @JoinColumn(name="permission_id"))
    private Set<UserPermission> permissions = new HashSet<>();

    public UserGroup() {}

    public UserGroup(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

}
