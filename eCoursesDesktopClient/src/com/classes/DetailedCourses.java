package com.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "detailedCourses")
public class DetailedCourses implements Serializable {

    private List<DetailedCourse> courses;

    public DetailedCourses() {
    }

    @XmlElement(name = "course_details")
    public List<DetailedCourse> getCourses() {
        if (courses == null) {
            courses = new ArrayList<DetailedCourse>();
        }
        return courses;
    }

    public void setCourses(List<DetailedCourse> courses) {
        this.courses = courses;
    }

}
