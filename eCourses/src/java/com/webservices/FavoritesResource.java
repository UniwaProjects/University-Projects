package com.webservices;

import com.classes.LearningOutcome;
import com.classes.SimpleCourse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("favorites")
public class FavoritesResource {

    FavoritesDAO favoritesDao = new FavoritesDAO();

    @GET
    @Path("/courses/{studid}")
    @Produces(MediaType.APPLICATION_XML)
    public List<SimpleCourse> getFavoriteCourses(@PathParam("studid") int studid) {
        return favoritesDao.getFavoriteCourses(studid);
    }

    @POST
    @Path("/courses/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response insertFavCourse(
            @FormParam("userid") int userid,
            @FormParam("courseid") int courseid,
            @FormParam("page") String page) throws URISyntaxException {

        favoritesDao.insertFavoriteCourse(userid, courseid);
        if (page != null) {
            return Response.temporaryRedirect(new URI(page)).build();
        } else {
            return Response.ok("Course added: " + courseid).build();
        }
    }

    @POST
    @Path("/courses/remove")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response removeFavCourse(
            @FormParam("userid") int userid,
            @FormParam("courseid") int courseid,
            @FormParam("page") String page) throws URISyntaxException {

        favoritesDao.removeFavoriteCourse(userid, courseid);
        if (page != null) {
            return Response.temporaryRedirect(new URI(page)).build();
        } else {
            return Response.ok("Course removed: " + courseid).build();
        }
    }

    @POST
    @Path("/outcomes/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response insertFavOutcome(
            @FormParam("userid") int userid,
            @FormParam("outcomeid") String outcomeid,
            @FormParam("page") String page) throws URISyntaxException {

        favoritesDao.insertFavoriteOutcome(userid, outcomeid);
        if (page != null) {
            return Response.temporaryRedirect(new URI(page)).build();
        } else {
            return Response.ok("Outcome added: " + outcomeid).build();
        }
    }

    @POST
    @Path("/outcomes/remove")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response removeFavOutcome(
            @FormParam("userid") int userid,
            @FormParam("outcomeid") String outcomeid,
            @FormParam("page") String page) throws URISyntaxException {

        favoritesDao.removeFavoriteOutcome(userid, outcomeid);
        if (page != null) {
            return Response.temporaryRedirect(new URI(page)).build();
        } else {
            return Response.ok("Outcome removed: " + outcomeid).build();
        }
    }

    @GET
    @Path("/outcomes/{studid}")
    @Produces(MediaType.APPLICATION_XML)
    public List<LearningOutcome> getFavOutcomes(@PathParam("studid") int studid) {
        return favoritesDao.getFavOutcomes(studid);
    }

}
