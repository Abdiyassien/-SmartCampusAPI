package com.mycompany.smartcampusapi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = new ArrayList<>(DataStore.getSensors().values());
        if (type != null && !type.isEmpty()) {
            sensors.removeIf(s -> !s.getType().equalsIgnoreCase(type));
        }
        return Response.ok(sensors).build();
    }

    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(400).entity("{\"error\":\"Sensor ID is required\"}").build();
        }
        if (!DataStore.getRooms().containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room " + sensor.getRoomId() + " does not exist.");
        }
        if (DataStore.getSensors().containsKey(sensor.getId())) {
            return Response.status(409).entity("{\"error\":\"Sensor already exists\"}").build();
        }
        DataStore.getSensors().put(sensor.getId(), sensor);
        Room room = DataStore.getRooms().get(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());
        return Response.status(201).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(404).entity("{\"error\":\"Sensor not found\"}").build();
        }
        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}