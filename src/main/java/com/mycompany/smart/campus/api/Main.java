/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api;

/**
 *
 * @author Hussain
 */

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import com.mycompany.smart.campus.api.config.SmartCampusApp;

import java.net.URI;

public class Main {
    // 1. Change the base URI to root
    public static final String BASE_URI = "http://localhost:8080/";
    
    public static void main(String[] args) {
        try {
            final SmartCampusApp app = new SmartCampusApp();
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), app);
            
            System.out.println("🚀 Smart Campus API is running!");
            // 2. Adjust the print statement so you can easily click the correct link
            System.out.println("Test it at: " + BASE_URI + "api/v1/"); 
            System.out.println("Hit Ctrl-C to stop it...");
            
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}