/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.config;

/**
 *
 * @author Hussain
 */

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
        
@ApplicationPath("/api/v1")
public class SmartCampusApp extends ResourceConfig {
    public SmartCampusApp() {
        packages("com.mycompany.smart.campus.api");
    }
}
