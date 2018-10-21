package com.servlets;

import com.classes.Professor;
import com.classes.Student;
import com.classes.XMLHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class Registration extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String error = "Please fill out all the fields according to limitations.";
        request.setAttribute("error", error);

        if (request.getParameter("account") != null
                && request.getParameter("account").equals("student")) {

            if (request.getParameter("username").length() >= 4
                    && request.getParameter("username").length() <= 12
                    && request.getParameter("password").length() >= 6
                    && request.getParameter("password").length() <= 12) {

                Student s = new Student();
                s.setUsername(request.getParameter("username"));
                s.setPassword(request.getParameter("password"));
                try {

                    JAXBContext jaxbContext = JAXBContext.newInstance(Student.class);
                    String path = "users/student";
                    XMLHandler.marshallSend(path, s, jaxbContext);

                } catch (JAXBException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                request.getRequestDispatcher("signup.jsp?form=student")
                        .forward(request, response);
            }

        } else if (request.getParameter("account") != null
                && request.getParameter("account").equals("professor")) {

            if (request.getParameter("username").length() >= 4
                    && request.getParameter("username").length() <= 12
                    && request.getParameter("password").length() >= 6
                    && request.getParameter("password").length() <= 12
                    && request.getParameter("firstname").length() >= 2
                    && request.getParameter("firstname").length() <= 45
                    && request.getParameter("lastname").length() >= 2
                    && request.getParameter("lastname").length() <= 45) {

                Professor p = new Professor();
                p.setFirstname(request.getParameter("firstname"));
                p.setLastname(request.getParameter("lastname"));
                p.setEduLevel(request.getParameter("edulevel"));
                p.setUsername(request.getParameter("username"));
                p.setPassword(request.getParameter("password"));

                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Professor.class);
                    String path = "users/professor";
                    XMLHandler.marshallSend(path, p, jaxbContext);

                } catch (JAXBException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                request.getRequestDispatcher("signup.jsp?form=professor")
                        .forward(request, response);
            }
        } else {
            response.sendRedirect("index.jsp");
        }
        response.sendRedirect("index.jsp");
    }

}
