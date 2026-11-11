# Challenge HTTP Transcript: Orders API, HATEOAS, Idempotency, & OpenAPI

All calls were executed against the running Grizzly server (`http://localhost:8080/api/`). Write endpoints include `-H "X-Api-Key: demo-123"`.

---

## 1. OpenAPI Schema (`GET /api/openapi`)

Request:

```bash
curl -i -s "http://localhost:8080/api/openapi" | head -n 25
```

Response (`application/yaml`):

```http
HTTP/1.1 200 OK
X-Correlation-Id: 81a9f2c3-ac77-451f-835a-cf457ca863cb
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match,Idempotency-Key
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id,Idempotency-Replayed
Content-Type: application/yaml
Content-Length: 7868

---
openapi: 3.0.3
info:
  title: shop-rest API
  description: Jakarta REST 4.0 Product Catalogue & Order API
  version: 1.0.0
paths:
  /application.wadl:
    get:
      responses:
        "200":
          description: OK
  /application.wadl/{path}:
    get:
      parameters:
      - name: path
```

---

## 2. Missing `Idempotency-Key` Header (`428 Precondition Required`)

Request:

```bash
curl -i -s -X POST -H "X-Api-Key: demo-123" -H "Content-Type: application/json" \
  -d '{"lines":[{"productId":1,"quantity":2}]}' \
  "http://localhost:8080/api/orders"
```

Response:

```http
HTTP/1.1 428 Precondition Required
X-Correlation-Id: 890d6a94-5b9c-49bc-a07d-28a78ba0a0cc
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match,Idempotency-Key
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id,Idempotency-Replayed
Content-Type: application/problem+json
Content-Length: 135

{"detail":"Idempotency-Key header required","instance":"/api/orders","status":428,"title":"Precondition Required","type":"about:blank"}
```

---

## 3. Place Order with `Idempotency-Key` (`201 Created`, HATEOAS `_links`)

Request:

```bash
curl -i -s -X POST -H "X-Api-Key: demo-123" -H "Idempotency-Key: ord-100" -H "Content-Type: application/json" \
  -d '{"lines":[{"productId":1,"quantity":2},{"productId":2,"quantity":5}]}' \
  "http://localhost:8080/api/orders"
```

Response (`Idempotency-Replayed: false`):

```http
HTTP/1.1 201 Created
Location: http://localhost:8080/api/orders/o-64e000c6
Idempotency-Replayed: false
X-Correlation-Id: 0d978f20-8a3d-46dc-8262-0c1e7afccf14
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match,Idempotency-Key
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id,Idempotency-Replayed
Content-Type: application/json
Content-Length: 397

{"_links":{"product-1":"http://localhost:8080/api/products/1","pay":"http://localhost:8080/api/orders/o-64e000c6/payment","cancel":"http://localhost:8080/api/orders/o-64e000c6","self":"http://localhost:8080/api/orders/o-64e000c6","product-2":"http://localhost:8080/api/products/2"},"id":"o-64e000c6","lines":[{"productId":1,"quantity":2},{"productId":2,"quantity":5}],"status":"NEW","total":82.30}
```

---

## 4. Duplicate Idempotent Replay (`201 Created`, `Idempotency-Replayed: true`)

Request:

```bash
curl -i -s -X POST -H "X-Api-Key: demo-123" -H "Idempotency-Key: ord-100" -H "Content-Type: application/json" \
  -d '{"lines":[{"productId":1,"quantity":2},{"productId":2,"quantity":5}]}' \
  "http://localhost:8080/api/orders"
```

Response:

```http
HTTP/1.1 201 Created
Location: http://localhost:8080/api/orders/o-64e000c6
Idempotency-Replayed: true
X-Correlation-Id: 31a11f60-9f52-44e5-b460-d47deaca1642
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match,Idempotency-Key
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id,Idempotency-Replayed
Content-Type: application/json
Content-Length: 397

{"_links":{"product-1":"http://localhost:8080/api/products/1","pay":"http://localhost:8080/api/orders/o-64e000c6/payment","cancel":"http://localhost:8080/api/orders/o-64e000c6","self":"http://localhost:8080/api/orders/o-64e000c6","product-2":"http://localhost:8080/api/products/2"},"id":"o-64e000c6","lines":[{"productId":1,"quantity":2},{"productId":2,"quantity":5}],"status":"NEW","total":82.30}
```

---

## 5. `Idempotency-Key` Reused with Different Body (`422 Unprocessable Content`)

Request:

```bash
curl -i -s -X POST -H "X-Api-Key: demo-123" -H "Idempotency-Key: ord-100" -H "Content-Type: application/json" \
  -d '{"lines":[{"productId":1,"quantity":9}]}' \
  "http://localhost:8080/api/orders"
```

Response:

```http
HTTP/1.1 422 
X-Correlation-Id: ef170d5d-37ce-4b59-8f4b-010b9b936df6
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match,Idempotency-Key
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id,Idempotency-Replayed
Content-Type: application/problem+json
Content-Length: 148

{"detail":"Idempotency-Key reused with a different body","instance":"/api/orders","status":422,"title":"Unprocessable Content","type":"about:blank"}
```

---

## 6. Pay Order (`PUT /api/orders/{id}/payment`, `200 OK`)

Request:

```bash
curl -i -s -X PUT -H "X-Api-Key: demo-123" "http://localhost:8080/api/orders/o-64e000c6/payment"
```

Response (`pay` and `cancel` hypermedia links removed after payment):

```http
HTTP/1.1 200 OK
X-Correlation-Id: d15bfff1-d78f-4b12-a506-5d312df8c8c6
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match,Idempotency-Key
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id,Idempotency-Replayed
Content-Type: application/json
Content-Length: 283

{"_links":{"product-2":"http://localhost:8080/api/products/2","self":"http://localhost:8080/api/orders/o-64e000c6","product-1":"http://localhost:8080/api/products/1"},"id":"o-64e000c6","lines":[{"productId":1,"quantity":2},{"productId":2,"quantity":5}],"status":"PAID","total":82.30}
```

---

## 7. Cancel Paid Order Rejected (`409 Conflict`)

Request:

```bash
curl -i -s -X DELETE -H "X-Api-Key: demo-123" "http://localhost:8080/api/orders/o-64e000c6"
```

Response:

```http
HTTP/1.1 409 Conflict
X-Correlation-Id: 2aac7e9c-3db6-4f27-a842-1adfcf22f765
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match,Idempotency-Key
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id,Idempotency-Replayed
Content-Type: application/problem+json
Content-Length: 126

{"detail":"Cannot cancel paid order","instance":"/api/orders/o-64e000c6","status":409,"title":"Conflict","type":"about:blank"}
```

---

## 8. Cancel Unpaid Order & Stock Replenishment

### 8a. Stock Before Placing `ord-101`

```json
{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":10}
```

### 8b. Place `ord-101` (quantity `4`)

```http
HTTP/1.1 201 Created
Location: http://localhost:8080/api/orders/o-b96c6a84
Idempotency-Replayed: false
Content-Type: application/json

{"_links":{"cancel":"http://localhost:8080/api/orders/o-b96c6a84","pay":"http://localhost:8080/api/orders/o-b96c6a84/payment","self":"http://localhost:8080/api/orders/o-b96c6a84","product-1":"http://localhost:8080/api/products/1"},"id":"o-b96c6a84","lines":[{"productId":1,"quantity":4}],"status":"NEW","total":99.60}
```

### 8c. Stock Reserved (`10 -> 6`)

```json
{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":6}
```

### 8d. Cancel `ord-101` (`DELETE /api/orders/o-b96c6a84`, `200 OK`)

```http
HTTP/1.1 200 OK
Content-Type: application/json

{"_links":{"self":"http://localhost:8080/api/orders/o-b96c6a84","product-1":"http://localhost:8080/api/products/1"},"id":"o-b96c6a84","lines":[{"productId":1,"quantity":4}],"status":"CANCELLED","total":99.60}
```

### 8e. Stock Restored (`6 -> 10`)

```json
{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":10}
```
