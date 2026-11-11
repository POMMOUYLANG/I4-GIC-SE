package edu.itc.shop.api;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentMap;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import edu.itc.shop.api.dto.OrderDto;
import edu.itc.shop.api.dto.PlaceOrder;
import edu.itc.shop.domain.Order;
import edu.itc.shop.domain.OrderService;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    public record Stored(String bodyHash, Response.StatusType status, URI location, OrderDto body) {
    }

    private static final Jsonb JSONB = JsonbBuilder.create();

    private final OrderService orders;
    private final ConcurrentMap<String, Stored> idempotency;

    @Inject
    public OrderResource(OrderService orders, ConcurrentMap<String, Stored> idempotency) {
        this.orders = orders;
        this.idempotency = idempotency;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response place(
            @HeaderParam("Idempotency-Key") String key,
            @Valid @NotNull PlaceOrder cmd,
            @Context UriInfo uriInfo
    ) {
        if (key == null || key.isBlank()) {
            throw new ClientErrorException("Idempotency-Key header required", 428);
        }

        String hash = sha256(JSONB.toJson(cmd));
        boolean[] replayed = { true };

        Stored stored = idempotency.computeIfAbsent(key, k -> {
            replayed[0] = false;
            Order order = orders.place(cmd);
            URI loc = uriInfo.getAbsolutePathBuilder().path(order.id()).build();
            return new Stored(hash, Response.Status.CREATED, loc, OrderDto.from(order, uriInfo));
        });

        if (!stored.bodyHash().equals(hash)) {
            throw new ClientErrorException("Idempotency-Key reused with a different body", 422);
        }

        return Response.status(stored.status())
                .location(stored.location())
                .header("Idempotency-Replayed", replayed[0])
                .entity(stored.body())
                .build();
    }

    @GET
    @Path("{id}")
    public OrderDto get(@PathParam("id") String id, @Context UriInfo uriInfo) {
        return OrderDto.from(orders.get(id), uriInfo);
    }

    @PUT
    @Path("{id}/payment")
    public OrderDto pay(@PathParam("id") String id, @Context UriInfo uriInfo) {
        return OrderDto.from(orders.pay(id), uriInfo);
    }

    @DELETE
    @Path("{id}")
    public OrderDto cancel(@PathParam("id") String id, @Context UriInfo uriInfo) {
        return OrderDto.from(orders.cancel(id), uriInfo);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
