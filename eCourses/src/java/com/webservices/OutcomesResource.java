package com.webservices;

import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import com.classes.LearningOutcome;

@Path("outcomes")
public class OutcomesResource {

    OutcomesDAO outcomesDao = new OutcomesDAO();

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_XML)
    public List<LearningOutcome> getAll() {
        return outcomesDao.getAll();
    }

}
