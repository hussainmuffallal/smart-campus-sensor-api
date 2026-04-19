/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.exceptions;

/**
 *
 * @author Hussain
 */

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // JAX-RS Response.Status enum doesn't have 422 natively, so we pass the integer directly
        return Response.status(422)
                .entity("{\"error\": \"" + exception.getMessage() + "\", \"status\": 422}")
                .type("application/json")
                .build();
    }
}
