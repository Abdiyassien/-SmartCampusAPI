package com.mycompany.smartcampusapi;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", e.getClass().getName() + ": " + e.getMessage());
        return Response.status(500)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}