package com.webservices;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.classes.SimpleCourse;
import com.classes.DetailedCourse;
import com.classes.DetailedCourses;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CourseDAO {

    JDBC jdbc = new JDBC();

    public CourseDAO() {
        jdbc.connect();
    }

    private void SQLDebug(SQLException e) {
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

    private int returnCourseId(String englishTitle) {
        int id = 0;
        try {
            jdbc.r = jdbc.stmt.executeQuery("select course_id from courses where "
                    + "english_title = \"" + englishTitle + "\"");

            while (jdbc.r.next()) {
                id = jdbc.r.getInt(1);
            }

        } catch (SQLException ex) {
            SQLDebug(ex);
        }
        return id;
    }

    public void insert(DetailedCourse course) {
        try {
            jdbc.stmt.executeUpdate("insert into courses(prof_id, greek_title, "
                    + "english_title, edu_level, semester) values (\""
                    + course.getProfId() + "\", \""
                    + course.getGreekTitle() + "\", \""
                    + course.getEnglishTitle() + "\", \""
                    + course.getEduLevel() + "\", \""
                    + course.getSemester() + "\")");

            int id = returnCourseId(course.getEnglishTitle());

            for (String s : course.getOutcomes()) {
                jdbc.stmt.executeUpdate("insert into course_outcomes(course_id, "
                        + "outcome_id) values (\""
                        + id + "\", \""
                        + s + "\")");
            }

            for (String s : course.getReqCourses()) {
                jdbc.stmt.executeUpdate("insert into course_required_courses(course_id, "
                        + "req_course_id) values (\""
                        + id + "\", \""
                        + s + "\")");
            }

            for (String s : course.getReqOutcomes()) {
                jdbc.stmt.executeUpdate("insert into course_required_outcomes(course_id, "
                        + "outcome_id) values (\""
                        + id + "\", \""
                        + s + "\")");
            }
        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public void fileUpload(DetailedCourses courses) {
        for (DetailedCourse course : courses.getCourses()) {
            try {
                jdbc.stmt.executeUpdate("insert into courses(prof_id, greek_title, "
                        + "english_title, edu_level, semester) values (\""
                        + course.getProfId() + "\", \""
                        + course.getGreekTitle() + "\", \""
                        + course.getEnglishTitle() + "\", \""
                        + course.getEduLevel() + "\", \""
                        + course.getSemester() + "\")");

                int courseId = returnCourseId(course.getEnglishTitle());

                for (String s : course.getOutcomes()) {
                    jdbc.stmt.executeUpdate("insert into course_outcomes(course_id, "
                            + "outcome_id) values (\""
                            + courseId + "\", \""
                            + s + "\")");
                }

                for (String s : course.getReqCourses()) {
                    int reqCourseId = returnCourseId(s);
                    jdbc.stmt.executeUpdate("insert into course_required_courses(course_id, "
                            + "req_course_id) values (\""
                            + courseId + "\", \""
                            + reqCourseId + "\")");
                }

                for (String s : course.getReqOutcomes()) {
                    jdbc.stmt.executeUpdate("insert into course_required_outcomes(course_id, "
                            + "outcome_id) values (\""
                            + courseId + "\", \""
                            + s + "\")");
                }
            } catch (SQLException e) {
                SQLDebug(e);
            }
        }
    }

    public void delete(int course_id) {

        try {
            jdbc.stmt.executeUpdate("delete from courses where "
                    + "course_id = \"" + course_id + "\"");

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public List<SimpleCourse> getProfCourses(int id) {

        List<SimpleCourse> courses = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select course_id, greek_title, "
                    + "english_title from courses where prof_id = " + id);

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

    public List<SimpleCourse> getAll() {

        List<SimpleCourse> courses = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select course_id, greek_title, "
                    + "english_title from courses");

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

    public List<DetailedCourse> getDetailed() {

        List<DetailedCourse> courses = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select * from courses");

            while (jdbc.r.next()) {
                DetailedCourse c = new DetailedCourse();
                c.setCourseId(jdbc.r.getInt(1));
                c.setProfId(jdbc.r.getInt(2));
                c.setGreekTitle(jdbc.r.getString(3));
                c.setEnglishTitle(jdbc.r.getString(4));
                c.setEduLevel(jdbc.r.getString(5));
                c.setSemester(jdbc.r.getInt(6));
                courses.add(c);
            }

            //Professor
            for (DetailedCourse c : courses) {
                jdbc.r = jdbc.stmt.executeQuery("select lastname from professors "
                        + "where prof_id = " + c.getProfId());

                while (jdbc.r.next()) {
                    c.setProfessor(jdbc.r.getString(1));
                }
            }

            //Learning outcomes
            for (DetailedCourse c : courses) {
                jdbc.r = jdbc.stmt.executeQuery("select learning_outcomes.outcome_id from learning_outcomes, course_outcomes "
                        + "where learning_outcomes.outcome_id = course_outcomes.outcome_id "
                        + "and course_outcomes.course_id = " + c.getCourseId());

                List<String> outcomes = new ArrayList<>();
                while (jdbc.r.next()) {
                    outcomes.add(jdbc.r.getString(1));
                }
                c.setOutcomes(outcomes);
            }

            //Required Courses
            for (DetailedCourse c : courses) {
                jdbc.r = jdbc.stmt.executeQuery("select english_title from courses, "
                        + "course_required_courses where "
                        + "course_required_courses.req_course_id = courses.course_id "
                        + "and course_required_courses.course_id = " + c.getCourseId());

                List<String> reqCourses = new ArrayList<>();
                while (jdbc.r.next()) {
                    reqCourses.add(jdbc.r.getString(1));
                }
                c.setReqCourses(reqCourses);
            }

            //Required Learning outcomes
            for (DetailedCourse c : courses) {
                jdbc.r = jdbc.stmt.executeQuery("select learning_outcomes.outcome_id from learning_outcomes, course_required_outcomes "
                        + "where learning_outcomes.outcome_id = course_required_outcomes.outcome_id "
                        + "and course_required_outcomes.course_id = " + c.getCourseId());

                List<String> reqOutcomes = new ArrayList<>();
                while (jdbc.r.next()) {
                    reqOutcomes.add(jdbc.r.getString(1));
                }
                c.setReqOutcomes(reqOutcomes);
            }

        } catch (SQLException e) {
            SQLDebug(e);
        }

        return courses;
    }

    public DetailedCourse getByTitle(String title) {

        DetailedCourse c = new DetailedCourse();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select * from courses where "
                    + "english_title = \"" + title + "\" or greek_title = \"" + title + "\"");

            while (jdbc.r.next()) {
                c.setCourseId(jdbc.r.getInt(1));
                c.setProfId(jdbc.r.getInt(2));
                c.setGreekTitle(jdbc.r.getString(3));
                c.setEnglishTitle(jdbc.r.getString(4));
                c.setEduLevel(jdbc.r.getString(5));
                c.setSemester(jdbc.r.getInt(6));
            }

            //Professor
            jdbc.r = jdbc.stmt.executeQuery("select lastname from professors "
                    + "where prof_id = " + c.getProfId());

            while (jdbc.r.next()) {
                c.setProfessor(jdbc.r.getString(1));
            }

            //Learning outcomes
            jdbc.r = jdbc.stmt.executeQuery("select learning_outcomes.outcome_id from learning_outcomes, course_outcomes "
                    + "where learning_outcomes.outcome_id = course_outcomes.outcome_id "
                    + "and course_outcomes.course_id = " + c.getCourseId());

            List<String> outcomes = new ArrayList<>();
            while (jdbc.r.next()) {
                outcomes.add(jdbc.r.getString(1));
            }
            c.setOutcomes(outcomes);

            //Required Courses
            jdbc.r = jdbc.stmt.executeQuery("select english_title from courses, course_required_courses "
                    + "where course_required_courses.req_course_id = courses.course_id "
                    + "and course_required_courses.course_id = " + c.getCourseId());

            List<String> reqCourses = new ArrayList<>();
            while (jdbc.r.next()) {
                reqCourses.add(jdbc.r.getString(1));
            }
            c.setReqCourses(reqCourses);

            //Required Learning outcomes
            jdbc.r = jdbc.stmt.executeQuery("select learning_outcomes.outcome_id from learning_outcomes, course_required_outcomes "
                    + "where learning_outcomes.outcome_id = course_required_outcomes.outcome_id "
                    + "and course_required_outcomes.course_id = " + c.getCourseId());

            List<String> reqOutcomes = new ArrayList<>();
            while (jdbc.r.next()) {
                reqOutcomes.add(jdbc.r.getString(1));
            }
            c.setReqOutcomes(reqOutcomes);

        } catch (SQLException e) {
            SQLDebug(e);
        }
        return c;
    }

    public List<SimpleCourse> findWithTitle(String title) {

        List<SimpleCourse> courses = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select course_id, greek_title, "
                    + "english_title from courses where english_title like "
                    + "\"%" + title + "%\" or greek_title like \"%" + title + "%\"");

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

    public List<SimpleCourse> getCoursesByProf(String name) {

        List<SimpleCourse> courses = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select prof_id from professors "
                    + "where lastname like \"%" + name + "%\"");

            int profId = 0;
            while (jdbc.r.next()) {
                profId = jdbc.r.getInt(1);
            }

            jdbc.r = jdbc.stmt.executeQuery("select course_id, greek_title, "
                    + "english_title from courses where prof_id = " + profId);

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

    public List<SimpleCourse> findWithOutcome(String id) {

        List<SimpleCourse> courses = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select courses.course_id, greek_title, "
                    + "english_title from courses, course_outcomes "
                    + "where courses.course_id = course_outcomes.course_id "
                    + "and course_outcomes.outcome_id = \"" + id + "\"");

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

}
