package com.servlets;

import com.classes.Credentials;
import com.classes.Professor;
import com.classes.Token;
import com.classes.XMLHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class Authentication extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Credentials c = new Credentials();
        c.setUsername(request.getParameter("username"));
        c.setPassword(request.getParameter("password"));

        try {
            Token t = XMLHandler.authenticate(c);

            if (t.getUser().equals("professors")) {
                JAXBContext jaxbContext = JAXBContext.newInstance(Professor.class);
                String path = "users/professor/" + t.getId();
                Professor prof = (Professor) XMLHandler.getUnmarshall(path, jaxbContext);

                if (prof.isActivated() == false) {
                    throw new LoginException();
                }
            }
            session.setAttribute("user", t.getUser());
            session.setAttribute("userid", t.getId());
            session.setAttribute("token", t.getToken());

            response.sendRedirect("index.jsp");
        } catch (FailedLoginException e) {
            e.printStackTrace();
            String error = "Authentication failed, wrong credentials.";;
            request.setAttribute("error", error);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (LoginException e) {
            e.printStackTrace();
            String error = "Authentication failed, not an active account.";
            request.setAttribute("error", error);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (JAXBException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
