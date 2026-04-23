package com.mycompany.smartcampusapi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public Response getAllRooms() {
        try {
            List<Room> rooms = new ArrayList<>(DataStore.getRooms().values());
            return Response.ok(rooms).build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(400).entity("{\"error\":\"Room ID is required\"}").build();
        }
        if (DataStore.getRooms().containsKey(room.getId())) {
            return Response.status(409).entity("{\"error\":\"Room already exists\"}").build();
        }
        DataStore.getRooms().put(room.getId(), room);
        return Response.status(201).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        if (room == null) {
            return Response.status(404).entity("{\"error\":\"Room not found\"}").build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        if (room == null) {
            return Response.status(404).entity("{\"error\":\"Room not found\"}").build();
        }
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " still has sensors assigned.");
        }
        DataStore.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}