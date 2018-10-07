package com.neu.aws.distributed.resource;


import java.util.Map;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("myresource")
public class PingResource {

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.WILDCARD)
//    public Response createPet() {
//        Map<String, String> pong = new HashMap<>();
//        pong.put("pong", "Hello, World!");
//        return Response.status(200).entity(pong).build();
//    }
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public int postIt(String str){
        return str.length();
    }
}