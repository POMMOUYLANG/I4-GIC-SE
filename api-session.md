# Task 4: REST API Session Transcripts (`api-session.md`)

This document records the verification session executed against the running Spring Boot application for **Task 4: REST API with Spring MVC, Validation, and RFC 7807 Error Handling**.

---

### 1. Create a Product (`POST /api/products`)

**Request:**

```bash
curl -i -X POST localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"USB-C cable","price":7.90,"stock":25}'
```

**Response:**

```http
HTTP/1.1 201
Location: /api/products/6
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 19 Oct 2026 06:40:12 GMT

{"id":6,"name":"USB-C cable","price":7.90,"stock":25}
```

---

### 2. Trigger Validation Error (`POST` with invalid data)

**Request:**

```bash
curl -i -X POST localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"","price":-5.00,"stock":-2}'
```

**Response (`400 Bad Request` with field details):**

```http
HTTP/1.1 400
Content-Type: application/problem+json
Transfer-Encoding: chunked
Date: Mon, 19 Oct 2026 06:40:20 GMT
Connection: close

{"detail":"Invalid request content","instance":"/api/products","status":400,"title":"Bad Request","errors":[{"message":"Product name is required","field":"name"},{"message":"Price must be at least 0.01","field":"price"},{"message":"Stock must be zero or positive","field":"stock"}]}
```

---

### 3. Search Products with Query Filter (`GET /api/products?q=Mac`)

**Request:**

```bash
curl -i "http://localhost:8080/api/products?q=Mac"
```

**Response:**

```http
HTTP/1.1 200
Content-Type: application/json
Content-Length: 63
Date: Mon, 19 Oct 2026 06:40:23 GMT

[{"id":1,"name":"MacBook Pro 16\"","price":2499.00,"stock":10}]
```

---

### 4. Fetch Non-Existent Product (`GET /api/products/999`)

**Request:**

```bash
curl -i localhost:8080/api/products/999
```

**Response (`404 Not Found`):**

```http
HTTP/1.1 404
Content-Type: application/problem+json
Transfer-Encoding: chunked
Date: Mon, 19 Oct 2026 06:40:26 GMT

{"detail":"Product 999 not found","instance":"/api/products/999","status":404,"title":"Not Found"}
```

---

### 5. Trigger Stock Conflict (`PATCH /api/products/6/stock`)

**Request:**

```bash
curl -i -X PATCH localhost:8080/api/products/6/stock \
  -H 'Content-Type: application/json' \
  -d '{"delta":-30}'
```

**Response (`409 Conflict`):**

```http
HTTP/1.1 409
Content-Type: application/problem+json
Transfer-Encoding: chunked
Date: Mon, 19 Oct 2026 06:40:32 GMT

{"detail":"Product 6 has 25 units, cannot remove 30","instance":"/api/products/6/stock","status":409,"title":"Conflict"}
```

---

### 6. Delete a Product (`DELETE /api/products/6`)

**Request:**

```bash
curl -i -X DELETE localhost:8080/api/products/6
```

**Response (`204 No Content`):**

```http
HTTP/1.1 204
Date: Mon, 19 Oct 2026 06:40:35 GMT
```

---

### 7. Delete Non-Existent Product (`DELETE /api/products/6` again)

**Request:**

```bash
curl -i -X DELETE localhost:8080/api/products/6
```

**Response (`404 Not Found`):**

```http
HTTP/1.1 404
Content-Type: application/problem+json
Transfer-Encoding: chunked
Date: Mon, 19 Oct 2026 06:40:40 GMT

{"detail":"Product 6 not found","instance":"/api/products/6","status":404,"title":"Not Found"}
```

---

### 8. MockMvc Test Suite Execution

```text
[INFO] Running edu.itc.se.shop.web.ProductControllerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.159 s -- in edu.itc.se.shop.web.ProductControllerTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
