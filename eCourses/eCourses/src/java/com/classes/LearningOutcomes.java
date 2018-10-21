package com.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "learningOutcomes")
public class LearningOutcomes implements Serializable {

    private List<LearningOutcome> outcomes;

    public LearningOutcomes() {
    }

    @XmlElement(name = "learning_outcome")
    public List<LearningOutcome> getOutcomes() {
        if (outcomes == null) {
            outcomes = new ArrayList<>();
        }
        return outcomes;
    }

    public void setOutcomes(List<LearningOutcome> outcomes) {
        this.outcomes = outcomes;
    }

}
