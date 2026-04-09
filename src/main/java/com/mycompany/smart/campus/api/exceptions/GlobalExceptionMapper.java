/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.exceptions;

/**
 *
 * @author Hussain
 */

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// This mandatory tag tells the server to activate this class automatically
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable>{
    
    @Override
    public Response toResponse(Throwable exception) {
        // Default to a 500 Server Error for unexpected crashes (like NullPointerExceptions)
        int statusCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String message = exception.getMessage() != null ? exception.getMessage() : "An unexpected server error occurred.";

        // If the exception is a known WebApplicationException (like a 404 Not Found), keep its specific status
        if (exception instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) exception;
            statusCode = webEx.getResponse().getStatus();
        }

        // Wrap the error message in a clean, professional JSON structure
        String jsonError = String.format("{\"error\": \"%s\", \"status\": %d}", message, statusCode);

        // Return the safety net response
        return Response.status(statusCode)
                .entity(jsonError)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
