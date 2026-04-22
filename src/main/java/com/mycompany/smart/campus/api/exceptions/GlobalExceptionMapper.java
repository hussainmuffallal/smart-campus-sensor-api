/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.exceptions;

/**
 *
 * @author Hussain
 */

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// This mandatory tag tells the server to activate this class automatically
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    
    @Override
    public Response toResponse(Throwable exception) {
        // Force a completely generic 500 Server Error to prevent ANY information leakage
        String jsonError = "{\"error\": \"An unexpected server error occurred. Please contact the administrator.\", \"status\": 500}";

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(jsonError)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
