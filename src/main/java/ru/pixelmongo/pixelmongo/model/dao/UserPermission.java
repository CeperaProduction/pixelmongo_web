package ru.pixelmongo.pixelmongo.model.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "permission_list")
public class UserPermission implements GrantedAuthority{

    /**
     *
     */
    private static final long serialVersionUID = -6212932341520576065L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String value;

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getAuthority() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UserPermission) {
            return this.value.equals(((UserPermission) obj).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.value;
    }

}
