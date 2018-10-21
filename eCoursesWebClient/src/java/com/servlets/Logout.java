package com.servlets;

import com.classes.Token;
import com.classes.XMLHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class Logout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Token t = new Token();
        t.setUser(session.getAttribute("user").toString());
        t.setId(Integer.parseInt(session.getAttribute("userid").toString()));
        t.setToken(session.getAttribute("token").toString());

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Token.class);
            String path = "users/logout";
            XMLHandler.marshallSend(path, t, jaxbContext);

            session.setAttribute("user", null);
            session.setAttribute("userid", null);
            session.setAttribute("token", null);
            session.invalidate();

        } catch (JAXBException ex) {
            Logger.getLogger(Logout.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.sendRedirect("index.jsp");
    }

}
