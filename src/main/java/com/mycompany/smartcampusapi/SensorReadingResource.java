package com.mycompany.smartcampusapi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(404).entity("{\"error\":\"Sensor not found\"}").build();
        }
        List<SensorReading> readings = DataStore.getReadingsForSensor(sensorId);
        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(404).entity("{\"error\":\"Sensor not found\"}").build();
        }
        if ("MAINTENANCE".equals(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is under maintenance and cannot accept readings.");
        }
        SensorReading newReading = new SensorReading(reading.getValue());
        DataStore.getReadingsForSensor(sensorId).add(newReading);
        sensor.setCurrentValue(reading.getValue());
        return Response.status(201).entity(newReading).build();
    }
}