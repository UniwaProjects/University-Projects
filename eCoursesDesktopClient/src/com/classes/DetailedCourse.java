package com.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "course_details")
@XmlType(propOrder = {"courseId", "greekTitle", "englishTitle",
    "eduLevel", "semester", "profId", "professor", "outcomes", "reqCourses", "reqOutcomes"})
public class DetailedCourse implements Serializable {

    private int courseId;
    private int profId;
    private String greekTitle;
    private String englishTitle;
    private String eduLevel;
    private int semester;
    private String professor;
    private List<String> outcomes = new ArrayList<>();
    private List<String> reqCourses = new ArrayList<>();
    private List<String> reqOutcomes = new ArrayList<>();

    public DetailedCourse() {
    }

    @XmlElement(name = "course_id")
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @XmlElement(name = "prof_id")
    public int getProfId() {
        return profId;
    }

    public void setProfId(int profId) {
        this.profId = profId;
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

    @XmlElement(name = "edu_level")
    public String getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(String eduLevel) {
        this.eduLevel = eduLevel;
    }

    @XmlElement(name = "semester")
    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @XmlElement(name = "professor")
    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    @XmlElementWrapper(name = "learning_outcomes")
    @XmlElement(name = "learning_outcome")
    public List<String> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<String> outcomes) {
        this.outcomes = outcomes;
    }

    @XmlElementWrapper(name = "required_courses")
    @XmlElement(name = "req_courses")
    public List<String> getReqCourses() {
        return reqCourses;
    }

    public void setReqCourses(List<String> reqCourses) {
        this.reqCourses = reqCourses;
    }

    @XmlElementWrapper(name = "required_outcomes")
    @XmlElement(name = "req_outcome")
    public List<String> getReqOutcomes() {
        return reqOutcomes;
    }

    public void setReqOutcomes(List<String> reqOutcomes) {
        this.reqOutcomes = reqOutcomes;
    }

    @Override
    public String toString() {
        return "DetailedCourse{" + "courseId=" + courseId + ", profId=" + profId + ", greekTitle=" + greekTitle + ", englishTitle=" + englishTitle + ", eduLevel=" + eduLevel + ", semester=" + semester + ", professor=" + professor + ", outcomes=" + outcomes + ", reqCourses=" + reqCourses + ", reqOutcomes=" + reqOutcomes + '}';
    }
  
}
