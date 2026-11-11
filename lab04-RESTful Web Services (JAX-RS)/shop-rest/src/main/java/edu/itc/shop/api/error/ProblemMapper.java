package edu.itc.shop.api.error;

import java.lang.System.Logger.Level;
import java.util.UUID;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import edu.itc.shop.api.dto.Problem;

@Provider
public class ProblemMapper implements ExceptionMapper<Throwable> {

    private static final System.Logger LOG = System.getLogger("api");

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable ex) {
        String path = (uriInfo != null && uriInfo.getRequestUri() != null)
                ? uriInfo.getRequestUri().getPath()
                : "/";

        if (ex instanceof WebApplicationException w) {
            int status = w.getResponse().getStatus();
            Response.StatusType info = w.getResponse().getStatusInfo();
            String title = info != null ? info.getReasonPhrase() : "Error";
            String detail = extractDetail(w, title);
            var problem = new Problem("about:blank", title, status, detail, path);
            return Response.status(status)
                    .type(Problem.MEDIA_TYPE)
                    .entity(problem)
                    .build();
        }

        String ref = UUID.randomUUID().toString();
        LOG.log(Level.ERROR, "Unhandled exception [reference " + ref + "] at " + path, ex);

        var problem = new Problem(
                "about:blank",
                "Internal Server Error",
                500,
                "reference " + ref,
                path
        );

        return Response.status(500)
                .type(Problem.MEDIA_TYPE)
                .entity(problem)
                .build();
    }

    private static String extractDetail(WebApplicationException w, String fallback) {
        if (w.getCause() instanceof WebApplicationException cause
                && cause.getMessage() != null && !cause.getMessage().isBlank()) {
            return cause.getMessage();
        }
        return (w.getMessage() != null && !w.getMessage().isBlank()) ? w.getMessage() : fallback;
    }
}
