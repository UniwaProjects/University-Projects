package com.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "professors")
public class Professors implements Serializable {

    private List<Professor> profs;

    public Professors() {
    }

    @XmlElement(name = "professor")
    public List<Professor> getProfs() {
        if (profs == null) {
            profs = new ArrayList<>();
        }
        return profs;
    }

    public void setProfs(List<Professor> profs) {
        this.profs = profs;
    }

}
