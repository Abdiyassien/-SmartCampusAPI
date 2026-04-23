package com.mycompany.smartcampusapi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    static {
        // Add sample data so GET /rooms returns something
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        rooms.put(r1.getId(), r1);
    }

    public static Map<String, Room> getRooms() { return rooms; }
    public static Map<String, Sensor> getSensors() { return sensors; }
    public static Map<String, List<SensorReading>> getReadings() { return readings; }

    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        readings.putIfAbsent(sensorId, new ArrayList<>());
        return readings.get(sensorId);
    }
}