package com.classes;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "learning_outcome")
@XmlType(propOrder = {"id", "field", "category", "number", "description",
    "masteryLevel"})
public class LearningOutcome implements Serializable {

    private String id;
    private String field;
    private String category;
    private int number;
    private String description;
    private String masteryLevel;

    public LearningOutcome() {
    }

    @XmlElement(name = "outcome_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "field")
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @XmlElement(name = "category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlElement(name = "number")
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @XmlElement(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "mastery_level")
    public String getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(String masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.field);
        hash = 47 * hash + Objects.hashCode(this.category);
        hash = 47 * hash + this.number;
        hash = 47 * hash + Objects.hashCode(this.description);
        hash = 47 * hash + Objects.hashCode(this.masteryLevel);
        return hash;
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
        final LearningOutcome other = (LearningOutcome) obj;
        if (this.number != other.number) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.field, other.field)) {
            return false;
        }
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.masteryLevel, other.masteryLevel)) {
            return false;
        }
        return true;
    }



}
