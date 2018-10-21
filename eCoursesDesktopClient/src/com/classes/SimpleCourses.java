package com.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "simpleCourses")
public class SimpleCourses implements Serializable {

    private List<SimpleCourse> courses;

    public SimpleCourses() {
    }

    @XmlElement(name = "course_simple")
    public List<SimpleCourse> getCourses() {
        if (courses == null) {
            courses = new ArrayList<>();
        }
        return courses;
    }

    public void setCourses(List<SimpleCourse> courses) {
        this.courses = courses;
    }

}
