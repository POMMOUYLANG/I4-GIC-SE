package edu.itc.shop.api;

import java.io.InputStream;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/openapi")
public class OpenApiResource {

    @GET
    @Produces("application/yaml")
    public Response openapi() {
        InputStream is = getClass().getResourceAsStream("/META-INF/openapi.yaml");
        if (is == null) {
            throw new NotFoundException("OpenAPI definition not found");
        }
        return Response.ok(is).build();
    }
}
