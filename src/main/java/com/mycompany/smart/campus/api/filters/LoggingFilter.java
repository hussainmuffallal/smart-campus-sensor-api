/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.filters;

/**
 *
 * @author Hussain
 */

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

// This tells Jersey to activate this filter automatically
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // The standard Java logger
    private static final Logger logger = Logger.getLogger(LoggingFilter.class.getName());
    
    // 1. THIS RUNS WHEN A REQUEST COMES IN
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        
        logger.info(">>> INCOMING REQUEST: " + method + " /" + path);
        
        // We act like a stopwatch, recording the exact millisecond the request arrived
        requestContext.setProperty("startTime", System.currentTimeMillis());
    }

    // 2. THIS RUNS WHEN A RESPONSE GOES OUT
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // We check our stopwatch to see how long it took to process
        Long startTime = (Long) requestContext.getProperty("startTime");
        long executionTime = (startTime != null) ? (System.currentTimeMillis() - startTime) : 0;
        
        int status = responseContext.getStatus();
        
        logger.info("<<< OUTGOING RESPONSE: Status " + status + " | Execution Time: " + executionTime + "ms\n");
    }
}
