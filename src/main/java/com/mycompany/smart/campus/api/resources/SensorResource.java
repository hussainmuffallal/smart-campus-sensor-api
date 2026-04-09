/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.resources;

/**
 *
 * @author Hussain
 */

import com.mycompany.smart.campus.api.db.DataStore;
import com.mycompany.smart.campus.api.models.Sensor;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    
    private final DataStore db = DataStore.getInstance();

    // GET /api/v1/sensors?type={sensorType}
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> resultList;

        // If the user provided a '?type=' query, filter the list.
        // Otherwise, return all sensors.
        if (type != null && !type.trim().isEmpty()) {
            resultList = db.getSensors().values().stream()
                    .filter(sensor -> type.equalsIgnoreCase(sensor.getType()))
                    .collect(Collectors.toList());
        } else {
            resultList = List.copyOf(db.getSensors().values());
        }

        return Response.ok(resultList).build();
    }

    // POST /api/v1/sensors
    @POST
    public Response registerSensor(Sensor sensor) {
        // 1. Dependency Validation: Does the assigned room exist?
        if (!db.getRooms().containsKey(sensor.getRoomId())) {
            // NOTE: Returning a standard 400 for now. In Part 5, we upgrade 
            // this to a custom Exception Mapper returning a 422!
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Validation Failed: The specified roomId does not exist.\"}")
                    .build();
        }

        // 2. Generate an ID if one isn't provided
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            sensor.setId(UUID.randomUUID().toString());
        }

        // 3. Save the sensor
        db.getSensors().put(sensor.getId(), sensor);
        
        // 4. Update the Room to know about this new sensor
        db.getRooms().get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }
    // --- SUB-RESOURCE LOCATOR ---
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        
        // If the sensor doesn't exist, block the request
        if (!db.getSensors().containsKey(sensorId)) {
            throw new WebApplicationException(
                Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Sensor not found\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build()
            );
        }
        
        // Hand the request off to the child class
        return new SensorReadingResource(sensorId);
    }
}
