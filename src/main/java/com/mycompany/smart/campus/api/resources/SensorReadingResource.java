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
import com.mycompany.smart.campus.api.models.SensorReading;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class SensorReadingResource {
    
    private final String sensorId;
    private final DataStore db = DataStore.getInstance();

    // The constructor requires a sensorId so it knows exactly which sensor's data to look up
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        List<SensorReading> readings = db.getSensorReadings().get(sensorId);
        if (readings == null) {
            readings = new CopyOnWriteArrayList<>();
        }
        return Response.ok(readings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        
        // 0. State Constraint Validation (MUST HAPPEN FIRST!)
        com.mycompany.smart.campus.api.models.Sensor parentSensor = db.getSensors().get(sensorId);
        if (parentSensor != null && "MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new com.mycompany.smart.campus.api.exceptions.SensorUnavailableException("Sensor is currently in maintenance mode and cannot accept new readings.");
        }

        // Generate IDs
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // 1. Save the reading to the history list
        db.getSensorReadings()
          .computeIfAbsent(sensorId, k -> new CopyOnWriteArrayList<>())
          .add(reading);

        // 2. The Mandatory "Side Effect" (Updating the current value)
        if (parentSensor != null) {
            parentSensor.setCurrentValue(reading.getValue());
        }

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
