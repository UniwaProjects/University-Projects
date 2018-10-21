package com.classes;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "user_token")
@XmlType(propOrder = {"user", "id", "token"})
public class Token implements Serializable {

    private String user;
    private int id;
    private String token;

    public Token() {
    }

    @XmlElement(name = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @XmlElement(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Token{" + "user=" + user + ", id=" + id + ", token=" + token + '}';
    }

}
