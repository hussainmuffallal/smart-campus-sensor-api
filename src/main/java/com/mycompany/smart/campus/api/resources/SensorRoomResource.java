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
import com.mycompany.smart.campus.api.models.Room;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    
    private final DataStore db = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        return Response.ok(db.getRooms().values()).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = db.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Room not found\"}")
                           .build();
        }
        return Response.ok(room).build();
    }

    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            room.setId(UUID.randomUUID().toString());
        }
        db.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        if (!db.getRooms().containsKey(roomId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Block deletion if sensors are assigned
        boolean hasSensors = db.getSensors().values().stream()
                               .anyMatch(sensor -> roomId.equals(sensor.getRoomId()));
        
        if (hasSensors) {
            throw new com.mycompany.smart.campus.api.exceptions.RoomNotEmptyException("Cannot delete room. Active sensors assigned.");
        }

        db.getRooms().remove(roomId);
        return Response.noContent().build(); 
    }
}
