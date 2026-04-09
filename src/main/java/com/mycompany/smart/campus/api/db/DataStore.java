/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.db;

/**
 *
 * @author Hussain
 */

import com.mycompany.smart.campus.api.models.Room;
import com.mycompany.smart.campus.api.models.Sensor;
import com.mycompany.smart.campus.api.models.SensorReading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    
    private static DataStore instance;

    private final Map<String, Room> rooms;
    private final Map<String, Sensor> sensors;
    private final Map<String, List<SensorReading>> sensorReadings;

    private DataStore() {
        // ConcurrentHashMap prevents crashes when multiple users hit the API at once
        rooms = new ConcurrentHashMap<>();
        sensors = new ConcurrentHashMap<>();
        sensorReadings = new ConcurrentHashMap<>();

        // Pre-loading dummy data so we can test GET requests immediately
        Room dummyRoom = new Room("LIB-301", "Library Quiet Study", 50);
        rooms.put(dummyRoom.getId(), dummyRoom);
        
        Sensor dummySensor = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        sensors.put(dummySensor.getId(), dummySensor);
        dummyRoom.getSensorIds().add(dummySensor.getId());
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public Map<String, Room> getRooms() { return rooms; }
    public Map<String, Sensor> getSensors() { return sensors; }
    public Map<String, List<SensorReading>> getSensorReadings() { return sensorReadings; }
}
