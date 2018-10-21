package com.webservices;

import com.classes.LearningOutcome;
import com.classes.SimpleCourse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoritesDAO {

    JDBC jdbc = new JDBC();

    public FavoritesDAO() {
        jdbc.connect();
    }

    public void SQLDebug(SQLException e) {

        e.printStackTrace(System.err);
        System.err.println("SQLState: "
                + ((SQLException) e).getSQLState());

        System.err.println("Error Code: "
                + ((SQLException) e).getErrorCode());

        System.err.println("Message: " + e.getMessage());

        Throwable t = e.getCause();
        while (t != null) {
            System.out.println("Cause: " + t);
            t = t.getCause();
        }
    }

    public void insertFavoriteCourse(int stud_id, int course_id) {

        try {
            jdbc.stmt.executeUpdate("insert into favorite_courses(stud_id, course_id) "
                    + "values (\""
                    + stud_id + "\", \""
                    + course_id + "\")");

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public void removeFavoriteCourse(int stud_id, int course_id) {

        try {
            jdbc.stmt.executeUpdate("delete from favorite_courses where "
                    + "stud_id = \"" + stud_id + "\" and "
                    + "course_id = \"" + course_id + "\"");

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public List<SimpleCourse> getFavoriteCourses(int studid) {

        List<SimpleCourse> courses = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select courses.course_id, greek_title, "
                    + "english_title from courses, favorite_courses where "
                    + "favorite_courses.stud_id = \"" + studid + "\" "
                    + "and courses.course_id = favorite_courses.course_id");

            while (jdbc.r.next()) {
                SimpleCourse c = new SimpleCourse();
                c.setCourseId(jdbc.r.getInt(1));
                c.setGreekTitle(jdbc.r.getString(2));
                c.setEnglishTitle(jdbc.r.getString(3));
                courses.add(c);
            }

        } catch (SQLException e) {
            SQLDebug(e);
        }
        return courses;
    }

    public void insertFavoriteOutcome(int stud_id, String outcome_id) {

        try {
            jdbc.stmt.executeUpdate("insert into favorite_outcomes(stud_id, outcome_id) "
                    + "values (\""
                    + stud_id + "\", \""
                    + outcome_id + "\")");

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public void removeFavoriteOutcome(int stud_id, String outcome_id) {

        try {
            jdbc.stmt.executeUpdate("delete from favorite_outcomes where "
                    + "stud_id = \"" + stud_id + "\" and "
                    + "outcome_id = \"" + outcome_id + "\"");

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public List<LearningOutcome> getFavOutcomes(int studid) {

        List<LearningOutcome> outcomes = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select learning_outcomes.outcome_id, field, category, "
                    + "number, description, mastery_level from "
                    + "learning_outcomes, favorite_outcomes where "
                    + "favorite_outcomes.stud_id = \"" + studid + "\" "
                    + "and learning_outcomes.outcome_id = favorite_outcomes.outcome_id");

            while (jdbc.r.next()) {
                LearningOutcome lo = new LearningOutcome();
                lo.setId(jdbc.r.getString(1));
                lo.setField(jdbc.r.getString(2));
                lo.setCategory(jdbc.r.getString(3));
                lo.setNumber(jdbc.r.getInt(4));
                lo.setDescription(jdbc.r.getString(5));
                lo.setMasteryLevel(jdbc.r.getString(6));
                outcomes.add(lo);
            }

        } catch (SQLException e) {
            SQLDebug(e);
        }
        return outcomes;
    }

}
