package com.webservices;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.classes.LearningOutcome;

public class OutcomesDAO {

    JDBC jdbc = new JDBC();

    public OutcomesDAO() {
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

    public List<LearningOutcome> getAll() {

        List<LearningOutcome> outcomes = new ArrayList<>();
        try {
            jdbc.r = jdbc.stmt.executeQuery("select * from learning_outcomes");

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
