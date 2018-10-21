package com.webservices;

import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import com.classes.SimpleCourse;
import com.classes.DetailedCourse;
import com.classes.DetailedCourses;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

@Path("/courses")
public class CoursesResource {

    CourseDAO courseDao = new CourseDAO();

    @GET
    @Path("/professor/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public List<SimpleCourse> getProfCourses(@PathParam("id") int id) {
        return courseDao.getProfCourses(id);
    }

    @GET
    @Path("/{title}")
    @Produces(MediaType.APPLICATION_XML)
    public DetailedCourse getByTitle(@PathParam("title") String title) {
        return courseDao.getByTitle(title);
    }

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_XML)
    public Response insert(DetailedCourse course) {
        System.out.println(course.getGreekTitle());
        courseDao.insert(course);
        String result = "Course created: " + course;
        return Response.status(201).entity(result).build();
    }

    @POST
    @Path("/fileupload")
    @Consumes(MediaType.APPLICATION_XML)
    public Response insert(DetailedCourses dcs) {

        courseDao.fileUpload(dcs);
        System.out.println("*****METHOD:    INSERT FROM CSV*****");
        String result = "Insert Success";
        return Response.status(201).entity(result).build();
    }

    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response delete(
            @FormParam("id") int id,
            @FormParam("page") String page) throws URISyntaxException {
        courseDao.delete(id);
        if (page != null) {
            return Response.temporaryRedirect(new URI(page)).build();
        } else {
            return Response.ok("Course deleted: " + id).build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_XML)
    public List<SimpleCourse> getAll() {
        return courseDao.getAll();
    }

    @GET
    @Path("/all/details")
    @Produces(MediaType.APPLICATION_XML)
    public List<DetailedCourse> getDetailed() {
        return courseDao.getDetailed();
    }

    @GET
    @Path("/find/{title}")
    @Produces(MediaType.APPLICATION_XML)
    public List<SimpleCourse> findWithTitle(@PathParam("title") String title) {
        return courseDao.findWithTitle(title);
    }

    @GET
    @Path("find/professor/{name}")
    @Produces(MediaType.APPLICATION_XML)
    public List<SimpleCourse> getCoursesByProf(@PathParam("name") String name) {
        return courseDao.getCoursesByProf(name);
    }

    @GET
    @Path("/outcome/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public List<SimpleCourse> findWithOutcome(@PathParam("id") String id) {
        return courseDao.findWithOutcome(id);
    }

}
