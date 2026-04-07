/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api;

/**
 *
 * @author Hussain
 */

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        
        // Creating the metadata requested by the specification
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("version", "v1.0");
        metadata.put("contact", "admin@smartcampus.edu");
        
        // Creating the HATEOAS navigation links
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        
        metadata.put("endpoints", links);

        // Return a 200 OK with the JSON payload
        return Response.ok(metadata).build();
    }
}
