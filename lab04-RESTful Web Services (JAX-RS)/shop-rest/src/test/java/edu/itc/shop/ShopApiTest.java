package edu.itc.shop;

import java.util.Map;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShopApiTest extends JerseyTest {

    static {
        System.setProperty("jersey.config.test.container.port", "0");
    }

    @Override
    protected Application configure() {
        return Main.config();
    }

    @Test
    void test200Ok() {
        Response res = target("products/1").request().get();
        assertEquals(200, res.getStatus());
    }

    @Test
    void test201Created() {
        String json = "{\"name\":\"Toaster\",\"sku\":\"TS-01\",\"price\":15.00,\"stock\":5}";
        Response res = target("products").request()
                .header("X-Api-Key", "demo-123")
                .post(Entity.json(json));
        assertEquals(201, res.getStatus());
        assertNotNull(res.getLocation());
    }

    @Test
    void test204NoContent() {
        Response res = target("products/2").request()
                .header("X-Api-Key", "demo-123")
                .delete();
        assertEquals(204, res.getStatus());
    }

    @Test
    void test304NotModified() {
        Response first = target("products/1").request().get();
        String etag = first.getHeaderString(HttpHeaders.ETAG);
        Response second = target("products/1").request()
                .header(HttpHeaders.IF_NONE_MATCH, etag)
                .get();
        assertEquals(304, second.getStatus());
    }

    @Test
    void test400BadRequest() {
        Response res = target("products").queryParam("minPrice", "invalid").request().get();
        assertEquals(400, res.getStatus());
        assertEquals("application/problem+json", res.getMediaType().toString());
    }

    @Test
    void test401Unauthorized() {
        Response res = target("products").request()
                .post(Entity.json("{\"name\":\"Fail\",\"sku\":\"FL-01\",\"price\":1.00,\"stock\":1}"));
        assertEquals(401, res.getStatus());
    }

    @Test
    void test404NotFound() {
        Response res = target("products/9999").request().get();
        assertEquals(404, res.getStatus());
    }

    @Test
    void test406NotAcceptable() {
        Response res = target("products/1").request("application/custom-unknown").get();
        assertEquals(406, res.getStatus());
    }

    @Test
    void test409Conflict() {
        String json = "{\"name\":\"Kettle Duplicate\",\"sku\":\"KT-01\",\"price\":20.00,\"stock\":1}";
        Response res = target("products").request()
                .header("X-Api-Key", "demo-123")
                .post(Entity.json(json));
        assertEquals(409, res.getStatus());
    }

    @Test
    void test412PreconditionFailed() {
        String json = "{\"name\":\"Kettle Edit\",\"sku\":\"KT-01\",\"price\":25.00,\"stock\":10}";
        Response res = target("products/1").request()
                .header("X-Api-Key", "demo-123")
                .header(HttpHeaders.IF_MATCH, "\"invalid-etag\"")
                .put(Entity.json(json));
        assertEquals(412, res.getStatus());
    }

    @Test
    void test415UnsupportedMediaType() {
        Response res = target("products").request()
                .header("X-Api-Key", "demo-123")
                .post(Entity.text("plain text payload"));
        assertEquals(415, res.getStatus());
    }

    @Test
    void test422UnprocessableContent() {
        String bodyA = "{\"lines\":[{\"productId\":1,\"quantity\":1}]}";
        String bodyB = "{\"lines\":[{\"productId\":1,\"quantity\":2}]}";
        Response first = target("orders").request()
                .header("X-Api-Key", "demo-123")
                .header("Idempotency-Key", "key-422")
                .post(Entity.json(bodyA));
        assertEquals(201, first.getStatus());

        Response res = target("orders").request()
                .header("X-Api-Key", "demo-123")
                .header("Idempotency-Key", "key-422")
                .post(Entity.json(bodyB));
        assertEquals(422, res.getStatus());
    }

    @Test
    void test428PreconditionRequired() {
        String json = "{\"lines\":[{\"productId\":1,\"quantity\":1}]}";
        Response res = target("orders").request()
                .header("X-Api-Key", "demo-123")
                .post(Entity.json(json));
        assertEquals(428, res.getStatus());
    }

    @Test
    void testIdempotencyReplayAndStockReservedOnce() {
        int initialStock = target("products/1").request().get(JsonObject.class).getInt("stock");
        String body = "{\"lines\":[{\"productId\":1,\"quantity\":2}]}";

        Response r1 = target("orders").request()
                .header("X-Api-Key", "demo-123")
                .header("Idempotency-Key", "k-repeat")
                .post(Entity.json(body));
        Response r2 = target("orders").request()
                .header("X-Api-Key", "demo-123")
                .header("Idempotency-Key", "k-repeat")
                .post(Entity.json(body));

        assertEquals(201, r1.getStatus());
        assertEquals("false", r1.getHeaderString("Idempotency-Replayed"));
        assertEquals(201, r2.getStatus());
        assertEquals("true", r2.getHeaderString("Idempotency-Replayed"));

        int afterStock = target("products/1").request().get(JsonObject.class).getInt("stock");
        assertEquals(initialStock - 2, afterStock);
    }

    @Test
    void testOrderLifecycleHateoasAndStockRestoration() {
        int initialStock = target("products/1").request().get(JsonObject.class).getInt("stock");
        String body = "{\"lines\":[{\"productId\":1,\"quantity\":3}]}";

        // Place order -> NEW with self, pay, cancel, product-1 links
        JsonObject placed = target("orders").request()
                .header("X-Api-Key", "demo-123")
                .header("Idempotency-Key", "k-cancel")
                .post(Entity.json(body), JsonObject.class);
        String orderId = placed.getString("id");
        assertEquals("NEW", placed.getString("status"));
        JsonObject links = placed.getJsonObject("_links");
        assertTrue(links.containsKey("self"));
        assertTrue(links.containsKey("pay"));
        assertTrue(links.containsKey("cancel"));
        assertTrue(links.containsKey("product-1"));

        int reservedStock = target("products/1").request().get(JsonObject.class).getInt("stock");
        assertEquals(initialStock - 3, reservedStock);

        // Cancel order -> CANCELLED, restores stock, pay/cancel links removed
        JsonObject cancelled = target("orders/" + orderId).request()
                .header("X-Api-Key", "demo-123")
                .delete(JsonObject.class);
        assertEquals("CANCELLED", cancelled.getString("status"));
        JsonObject cancelLinks = cancelled.getJsonObject("_links");
        assertTrue(cancelLinks.containsKey("self"));
        assertFalse(cancelLinks.containsKey("pay"));
        assertFalse(cancelLinks.containsKey("cancel"));

        int restoredStock = target("products/1").request().get(JsonObject.class).getInt("stock");
        assertEquals(initialStock, restoredStock);

        // Paying a cancelled order fails with 409 Conflict
        Response payCancelled = target("orders/" + orderId + "/payment").request()
                .header("X-Api-Key", "demo-123")
                .put(Entity.json(Map.of()));
        assertEquals(409, payCancelled.getStatus());

        // Place another order and pay it -> PAID; cancelling a paid order fails with 409
        JsonObject second = target("orders").request()
                .header("X-Api-Key", "demo-123")
                .header("Idempotency-Key", "k-pay")
                .post(Entity.json("{\"lines\":[{\"productId\":1,\"quantity\":1}]}"), JsonObject.class);
        String secondId = second.getString("id");

        JsonObject paid = target("orders/" + secondId + "/payment").request()
                .header("X-Api-Key", "demo-123")
                .put(Entity.json(Map.of()), JsonObject.class);
        assertEquals("PAID", paid.getString("status"));
        assertFalse(paid.getJsonObject("_links").containsKey("pay"));
        assertFalse(paid.getJsonObject("_links").containsKey("cancel"));

        Response cancelPaid = target("orders/" + secondId).request()
                .header("X-Api-Key", "demo-123")
                .delete();
        assertEquals(409, cancelPaid.getStatus());
    }

    @Test
    void testOpenApiYamlServed() {
        Response res = target("openapi").request().get();
        assertEquals(200, res.getStatus());
        assertEquals("application/yaml", res.getMediaType().toString());
        String yaml = res.readEntity(String.class);
        assertTrue(yaml.contains("shop-rest API"));
        assertTrue(yaml.contains("/orders:"));
        assertTrue(yaml.contains("/products:"));
    }
}
