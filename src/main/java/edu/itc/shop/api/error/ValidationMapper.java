package edu.itc.shop.api.error;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import edu.itc.shop.api.dto.Problem;

@Provider
public class ValidationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        List<Map<String, String>> errors = ex.getConstraintViolations().stream()
                .map(v -> Map.of(
                        "field", lastNode(v.getPropertyPath()),
                        "message", v.getMessage()
                ))
                .sorted(Comparator.comparing(m -> m.get("field")))
                .toList();

        String path = (uriInfo != null && uriInfo.getRequestUri() != null)
                ? uriInfo.getRequestUri().getPath()
                : "/";

        var problem = new Problem(
                "about:blank",
                "Invalid request",
                400,
                "Validation failed",
                path,
                errors
        );

        return Response.status(400)
                .type(Problem.MEDIA_TYPE)
                .entity(problem)
                .build();
    }

    static String lastNode(Path path) {
        String name = null;
        for (Path.Node n : path) {
            if (n.getName() != null) {
                name = n.getName();
            }
        }
        return name != null ? name : path.toString();
    }
}
