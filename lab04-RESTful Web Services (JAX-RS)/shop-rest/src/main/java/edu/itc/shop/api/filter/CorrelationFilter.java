package edu.itc.shop.api.filter;

import java.lang.System.Logger.Level;
import java.util.UUID;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public static final String HEADER = "X-Correlation-Id";
    private static final String PROP_ID = "shop.correlationId";
    private static final String PROP_START = "shop.startNanoTime";
    private static final System.Logger LOG = System.getLogger("api");

    @Override
    public void filter(ContainerRequestContext req) {
        String id = resolveId(req);
        req.setProperty(PROP_ID, id);
        req.setProperty(PROP_START, System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) {
        String id = (String) req.getProperty(PROP_ID);
        if (id == null) {
            id = resolveId(req);
        }
        res.getHeaders().putSingle(HEADER, id);

        Long start = (Long) req.getProperty(PROP_START);
        long ms = start != null ? (System.nanoTime() - start) / 1_000_000 : 0L;

        LOG.log(Level.INFO, "{0} {1} -> {2} [{3}] {4} ms",
                req.getMethod(),
                req.getUriInfo().getRequestUri().getPath(),
                res.getStatus(),
                id,
                ms);
    }

    private static String resolveId(ContainerRequestContext req) {
        String header = req.getHeaderString(HEADER);
        return (header != null && !header.isBlank()) ? header.trim() : UUID.randomUUID().toString();
    }
}
