package com.servlets;

import com.classes.DetailedCourse;
import com.classes.XMLHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class NewCourse extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        if (request.getParameter("greektitle").length() >= 2
                && request.getParameter("greektitle").length() <= 45
                && request.getParameter("englishtitle").length() >= 2
                && request.getParameter("englishtitle").length() <= 45) {

            DetailedCourse course = new DetailedCourse();
            course.setProfId(Integer.parseInt(session.getAttribute("userid").toString()));
            course.setEnglishTitle(request.getParameter("englishtitle"));
            course.setGreekTitle(request.getParameter("greektitle"));
            course.setEduLevel(request.getParameter("edulevel"));
            course.setSemester(Integer.parseInt(request.getParameter("semester")));

            String[] outcome = request.getParameterValues("outcome");
            List<String> outcomes = new ArrayList<>();
            if (outcome != null) {
                for (int i = 0; i < outcome.length; i++) {
                    outcomes.add(outcome[i]);
                }
                course.setOutcomes(outcomes);
            }

            String[] reqCourse = request.getParameterValues("reqcourse");
            List<String> reqCourses = new ArrayList<>();
            if (reqCourse != null) {
                for (int i = 0; i < reqCourse.length; i++) {
                    reqCourses.add(reqCourse[i]);
                }
                course.setReqCourses(reqCourses);
            }

            String[] reqOutcome = request.getParameterValues("reqoutcome");
            List<String> reqOutcomes = new ArrayList<>();
            if (reqOutcome != null) {
                for (int i = 0; i < reqOutcome.length; i++) {
                    reqOutcomes.add(reqOutcome[i]);
                }
                course.setReqOutcomes(reqOutcomes);
            }

            try {
                JAXBContext jaxbContext;
                jaxbContext = JAXBContext.newInstance(DetailedCourse.class);
                String path = "courses/insert";
                XMLHandler.marshallSend(path, course, jaxbContext);
            } catch (JAXBException ex) {
                Logger.getLogger(NewCourse.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            String error = "Please fill out all the fields according to limitations.";
            request.setAttribute("error", error);
            request.getRequestDispatcher("new-course.jsp").forward(request, response);
        }
        response.sendRedirect("index.jsp");
    }

}
