package com.webservices;

import com.classes.Credentials;
import com.classes.Professor;
import com.classes.Student;
import com.classes.Token;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.security.auth.login.FailedLoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
public class UsersResource {

    private UserDAO userDao = new UserDAO();
    private TokenManager tokenManager = new TokenManager();

    @POST
    @Path("/student")
    @Consumes(MediaType.APPLICATION_XML)
    public Response insertStudent(Student student) {

        userDao.insertStudent(student);
        System.out.println("*****METHOD:    INSERT*****");
        String result = "Student created: " + student;
        return Response.status(201).entity(result).build();
    }

    @POST
    @Path("/professor")
    @Consumes(MediaType.APPLICATION_XML)
    public Response insertProfessor(Professor professor) {

        userDao.insertProfessor(professor);
        System.out.println("*****METHOD:    INSERT*****");
        String result = "Professor created: " + professor;
        return Response.status(201).entity(result).build();
    }

    @POST
    @Path("/professor/activate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response activateProfessor(
            @FormParam("id") int id,
            @FormParam("page") String page) throws URISyntaxException {

        userDao.activateProfessor(id);
        if (page != null) {
            return Response.temporaryRedirect(new URI(page)).build();
        } else {
            return Response.ok("Professor activated: " + id).build();
        }
    }

    @POST
    @Path("/professor/deactivate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response deactivateProfessor(
            @FormParam("id") int id,
            @FormParam("page") String page) throws URISyntaxException {

        userDao.deactivateProfessor(id);
        if (page != null) {
            return Response.temporaryRedirect(new URI(page)).build();
        } else {
            return Response.ok("Professor deactivated: " + id).build();
        }
    }

    @GET
    @Path("/professor/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Professor getProfessor(@PathParam("id") int id) {
        return userDao.getProfessor(id);
    }

    @GET
    @Path("/professors")
    @Produces(MediaType.APPLICATION_XML)
    public List<Professor> getProfessors() {
        return userDao.getProfessors();
    }

    @POST
    @Path("/authentication")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response authenticate(Credentials credentials) {
        try {
            Token t = userDao.authenticate(credentials);
            t.setToken(tokenManager.issueToken());
            System.out.println("*****METHOD:    AUTHENTICATE*****");
            return Response.ok(t).build();
        } catch (FailedLoginException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_XML)
    public Response logout(Token token) {
        tokenManager.removeToken(token.getToken());
        System.out.println("*****METHOD:    LOGOUT*****");
        return Response.ok("Removed token: " + token.getToken()).build();
    }

}
