package distributedsystem.resource;


import distributedsystem.resource.DAO.DailyCounterDAO;
import distributedsystem.resource.DO.DailyCounter;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
public class PingResource {

    DailyCounterDAO dailyCounterDAO = new DailyCounterDAO();


    @POST
    @Path("/{userID}/{dayID}/{timeInterval}/{stepCount}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String postData(@PathParam("userID") String userID,
                           @PathParam("dayID") String dayID,
                           @PathParam("timeInterval") String timeInterval,
                           @PathParam("stepCount") String stepCount) throws SQLException {

        DailyCounter dailyCounter = new DailyCounter(Integer.parseInt(userID),
                Integer.parseInt(dayID),
                Integer.parseInt(timeInterval),
                Integer.parseInt(stepCount));
        dailyCounterDAO.insert(dailyCounter);
        return "OK!";
    }




    @GET
    @Path("/single/{userID}/{dayID}")
    @Produces(MediaType.TEXT_PLAIN)
    public int getByDay(@PathParam("userID") String userID,
                        @PathParam("dayID") String dayID) throws SQLException {
        int sum = dailyCounterDAO.getCountForDay(Integer.parseInt(userID), Integer.parseInt(dayID));
        return sum;
    }


    @GET
    @Path("/current/{userID}")
    @Produces(MediaType.TEXT_PLAIN)
    public int getByDay(@PathParam("userID") String userID) throws SQLException {
        int sum = dailyCounterDAO.getCurrentDay(Integer.parseInt(userID));
        return sum;
    }



    @DELETE
    @Path("/clear")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteTable() throws SQLException {
        dailyCounterDAO.clear();
        return "Clear!";
    }





//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.WILDCARD)
//    public Response createPet() {
//        Map<String, String> pong = new HashMap<>();
//        pong.put("pong", "Hello, World!");
//        return Response.status(200).entity(pong).build();
//    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getIt() {
//        return "Got it!";
//    }
//
//    @POST
//    @Consumes(MediaType.TEXT_PLAIN)
//    public int postIt(String str){
//        return str.length();
//    }


}