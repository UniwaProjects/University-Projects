package com.classes;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "course_simple")
@XmlType(propOrder = {"courseId", "greekTitle", "englishTitle"})
public class SimpleCourse implements Serializable {

    private int courseId;
    private String greekTitle;
    private String englishTitle;

    public SimpleCourse() {
    }

    @XmlElement(name = "course_id")
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @XmlElement(name = "greek_title")
    public String getGreekTitle() {
        return greekTitle;
    }

    public void setGreekTitle(String greekTitle) {
        this.greekTitle = greekTitle;
    }

    @XmlElement(name = "english_title")
    public String getEnglishTitle() {
        return englishTitle;
    }

    public void setEnglishTitle(String englishTitle) {
        this.englishTitle = englishTitle;
    }

    @Override
    public String toString() {
        return "{" + courseId + "," + greekTitle + "," + englishTitle + '}';
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleCourse other = (SimpleCourse) obj;
        if (this.courseId != other.courseId) {
            return false;
        }
        if (!Objects.equals(this.greekTitle, other.greekTitle)) {
            return false;
        }
        if (!Objects.equals(this.englishTitle, other.englishTitle)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.courseId;
        hash = 59 * hash + Objects.hashCode(this.greekTitle);
        hash = 59 * hash + Objects.hashCode(this.englishTitle);
        return hash;
    }
    
}
