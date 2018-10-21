package com.classes;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "professor")
@XmlType(propOrder = {"profId", "firstname", "lastname", "eduLevel",
    "username", "password", "activated"})
public class Professor implements Serializable {

    private int profId;
    private String firstname;
    private String lastname;
    private String eduLevel;
    private String username;
    private String password;
    private boolean activated;

    public Professor() {
    }

    @XmlElement(name = "prof_id")
    public int getProfId() {
        return profId;
    }

    public void setProfId(int profId) {
        this.profId = profId;
    }

    @XmlElement(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @XmlElement(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @XmlElement(name = "edu_level")
    public String getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(String eduLevel) {
        this.eduLevel = eduLevel;
    }

    @XmlElement(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElement(name = "activated")
    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "Professor{" + "profId=" + profId + ", firstname=" + firstname + ", lastname=" + lastname + ", eduLevel=" + eduLevel + ", username=" + username + ", password=" + password + ", activated=" + activated + '}';
    }

}
