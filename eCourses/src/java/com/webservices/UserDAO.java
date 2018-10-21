package com.webservices;

import com.classes.Credentials;
import com.classes.Professor;
import com.classes.Student;
import com.classes.Token;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.FailedLoginException;

public class UserDAO {

    JDBC jdbc = new JDBC();

    public UserDAO() {
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

    public void insertStudent(Student student) {

        try {
            jdbc.stmt.executeUpdate("insert into students(username, "
                    + "password) values (\"" + student.getUsername() + "\", \""
                    + student.getPassword() + "\")");

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public void insertProfessor(Professor professor) {

        try {
            jdbc.stmt.executeUpdate("insert into professors(firstname, lastname, "
                    + "edu_level ,username, password) values (\""
                    + professor.getFirstname() + "\", \""
                    + professor.getLastname() + "\", \""
                    + professor.getEduLevel() + "\", \""
                    + professor.getUsername() + "\", \""
                    + professor.getPassword() + "\")");

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public void activateProfessor(int id) {

        try {
            jdbc.stmt.executeUpdate("update professors set account_activated = true "
                    + "where prof_id = " + id);

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public void deactivateProfessor(int id) {

        try {
            jdbc.stmt.executeUpdate("update professors set account_activated = false "
                    + "where prof_id = " + id);

        } catch (SQLException e) {
            SQLDebug(e);
        }
    }

    public Professor getProfessor(int id) {

        Professor prof = new Professor();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select prof_id, firstname, lastname, "
                    + "account_activated from professors where prof_id = \"" + id + "\"");

            while (jdbc.r.next()) {
                prof.setProfId(jdbc.r.getInt(1));
                prof.setFirstname(jdbc.r.getString(2));
                prof.setLastname(jdbc.r.getString(3));
                prof.setActivated(jdbc.r.getBoolean(4));
            }

        } catch (SQLException e) {
            SQLDebug(e);
        }
        return prof;
    }

    public List<Professor> getProfessors() {

        List<Professor> profs = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select * from professors");

            while (jdbc.r.next()) {
                Professor p = new Professor();
                p.setProfId(jdbc.r.getInt(1));
                p.setFirstname(jdbc.r.getString(2));
                p.setLastname(jdbc.r.getString(3));
                p.setEduLevel(jdbc.r.getString(4));
                p.setActivated(jdbc.r.getBoolean(7));
                profs.add(p);
            }

        } catch (SQLException e) {
            SQLDebug(e);
        }
        return profs;
    }

    public Token authenticate(Credentials credentials) throws FailedLoginException {

        Token t = new Token();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select stud_id from students where "
                    + "username = \"" + credentials.getUsername() + "\" and "
                    + "password = \"" + credentials.getPassword() + "\"");
            if (jdbc.r.next()) {
                t.setUser("students");
                t.setId(jdbc.r.getInt(1));
            } else {
                jdbc.r = jdbc.stmt.executeQuery("select prof_id from professors where "
                        + "username = \"" + credentials.getUsername() + "\" and "
                        + "password = \"" + credentials.getPassword() + "\"");

                if (jdbc.r.next()) {
                    t.setUser("professors");
                    t.setId(jdbc.r.getInt(1));
                } else {

                    jdbc.r = jdbc.stmt.executeQuery("select admin_id from administrator where "
                            + "username = \"" + credentials.getUsername() + "\" and "
                            + "password = \"" + credentials.getPassword() + "\"");

                    if (jdbc.r.next()) {
                        t.setUser("administrator");
                        t.setId(jdbc.r.getInt(1));
                    } else {
                        throw new FailedLoginException("FAILED: Incorrect credinentals.");
                    }
                }
            }
        } catch (SQLException e) {
            SQLDebug(e);
        }
        return t;
    }

}
