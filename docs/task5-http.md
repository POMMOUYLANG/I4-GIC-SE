# Task 5 HTTP Transcript: Pagination, ETag Caching, Versioning, and Sub-Resources

All calls were executed against the running Grizzly server (`http://localhost:8080/api/`). Write endpoints include `-H "X-Api-Key: demo-123"`.

---

## 1. Pagination — First Page (`page=0&size=5`)

Request:

```bash
curl -i -s "http://localhost:8080/api/products?page=0&size=5"
```

Response (`prev` omitted on page 0):

```http
HTTP/1.1 200 OK
Link: <http://localhost:8080/api/products?size=5&page=0>; rel="first"
Link: <http://localhost:8080/api/products?size=5&page=1>; rel="next"
Link: <http://localhost:8080/api/products?size=5&page=6>; rel="last"
X-Total-Count: 33
X-Correlation-Id: bf7cb4a9-8e88-4ae2-8979-a593ef004b91
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 320

[{"id":4,"name":"Item 04","price":9.50,"sku":"IT-04","stock":4},{"id":5,"name":"Item 05","price":10.50,"sku":"IT-05","stock":0},{"id":6,"name":"Item 06","price":11.50,"sku":"IT-06","stock":6},{"id":7,"name":"Item 07","price":12.50,"sku":"IT-07","stock":7},{"id":8,"name":"Item 08","price":13.50,"sku":"IT-08","stock":8}]
```

---

## 2. Pagination — Middle Page (`page=1&size=10`)

Request:

```bash
curl -i -s "http://localhost:8080/api/products?page=1&size=10"
```

Response (`first`, `prev`, `next`, and `last` present):

```http
HTTP/1.1 200 OK
Link: <http://localhost:8080/api/products?size=10&page=0>; rel="first"
Link: <http://localhost:8080/api/products?size=10&page=0>; rel="prev"
Link: <http://localhost:8080/api/products?size=10&page=2>; rel="next"
Link: <http://localhost:8080/api/products?size=10&page=3>; rel="last"
X-Total-Count: 33
X-Correlation-Id: 86c024e2-130a-43a6-9d8e-f9d0b3fc99db
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 659

[{"id":14,"name":"Item 14","price":19.50,"sku":"IT-14","stock":14},{"id":15,"name":"Item 15","price":20.50,"sku":"IT-15","stock":0},{"id":16,"name":"Item 16","price":21.50,"sku":"IT-16","stock":16},{"id":17,"name":"Item 17","price":22.50,"sku":"IT-17","stock":17},{"id":18,"name":"Item 18","price":23.50,"sku":"IT-18","stock":18},{"id":19,"name":"Item 19","price":24.50,"sku":"IT-19","stock":19},{"id":20,"name":"Item 20","price":25.50,"sku":"IT-20","stock":0},{"id":21,"name":"Item 21","price":26.50,"sku":"IT-21","stock":21},{"id":22,"name":"Item 22","price":27.50,"sku":"IT-22","stock":22},{"id":23,"name":"Item 23","price":28.50,"sku":"IT-23","stock":23}]
```

---

## 3. Pagination — Last Page (`page=3&size=10`)

Request:

```bash
curl -i -s "http://localhost:8080/api/products?page=3&size=10"
```

Response (`next` omitted on last page):

```http
HTTP/1.1 200 OK
Link: <http://localhost:8080/api/products?size=10&page=0>; rel="first"
Link: <http://localhost:8080/api/products?size=10&page=2>; rel="prev"
Link: <http://localhost:8080/api/products?size=10&page=3>; rel="last"
X-Total-Count: 33
X-Correlation-Id: 8fe229c4-81ca-4429-bb68-0c39cd4c9f80
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 184

[{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":12},{"id":2,"name":"Mug","price":6.50,"sku":"MG-01","stock":40},{"id":3,"name":"Tea","price":4.20,"sku":"TE-01","stock":0}]
```

---

## 4. Pagination Validation (`page=-1&size=200`)

Request:

```bash
curl -i -s "http://localhost:8080/api/products?page=-1&size=200"
```

Response (`400 Bad Request` as `application/problem+json`):

```http
HTTP/1.1 400 Bad Request
X-Correlation-Id: df7ebe0f-daa8-42f3-9448-244f583f8436
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/problem+json
Connection: close
Content-Length: 255

{"detail":"Validation failed","errors":[{"message":"must be greater than or equal to 0","field":"page"},{"message":"must be less than or equal to 100","field":"size"}],"instance":"/api/products","status":400,"title":"Invalid request","type":"about:blank"}
```

---

## 5. ETag & Cache-Control on Default v1 GET

Request:

```bash
curl -i -s "http://localhost:8080/api/products/1"
```

Response (`application/json` v1 `ProductDto`, `ETag`, and `Cache-Control`):

```http
HTTP/1.1 200 OK
ETag: "1"
Cache-Control: private, no-transform, max-age=60
X-Correlation-Id: 4351a6c2-c691-4beb-8b89-5adf169a607e
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 63

{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":12}
```

---

## 6. Conditional GET with `If-None-Match` (`304 Not Modified`)

Request:

```bash
curl -i -s -H 'If-None-Match: "1"' "http://localhost:8080/api/products/1"
```

Response:

```http
HTTP/1.1 304 Not Modified
ETag: "1"
X-Correlation-Id: a833d420-e8af-4e65-834a-191dee262e73
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
```

---

## 7. Optimistic Locking — Missing `If-Match` on `PUT` (`428 Precondition Required`)

Request:

```bash
curl -i -s -X PUT -H "X-Api-Key: demo-123" -H "Content-Type: application/json" \
  -d '{"name":"Kettle Pro","sku":"KT-01","price":29.90,"stock":12}' \
  "http://localhost:8080/api/products/1"
```

Response:

```http
HTTP/1.1 428 Precondition Required
X-Correlation-Id: c4573587-c593-4067-bb4b-01b3093dc2d3
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/problem+json
Content-Length: 135

{"detail":"If-Match header is required","instance":"/api/products/1","status":428,"title":"Precondition Required","type":"about:blank"}
```

---

## 8. Optimistic Locking — Stale `If-Match` (`412`) & Valid `If-Match` (`200`) + `PATCH /stock`

### 8a. Stale `If-Match` (`412 Precondition Failed`)

Request:

```bash
curl -i -s -X PUT -H "X-Api-Key: demo-123" -H 'If-Match: "999"' -H "Content-Type: application/json" \
  -d '{"name":"Kettle Pro","sku":"KT-01","price":29.90,"stock":12}' \
  "http://localhost:8080/api/products/1"
```

Response:

```http
HTTP/1.1 412 Precondition Failed
X-Correlation-Id: daa706c0-871d-46aa-9fca-b55391478913
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/problem+json
Content-Length: 141

{"detail":"ETag does not match current version","instance":"/api/products/1","status":412,"title":"Precondition Failed","type":"about:blank"}
```

### 8b. Matching `If-Match` (`200 OK` with incremented `ETag`)

Request:

```bash
curl -i -s -X PUT -H "X-Api-Key: demo-123" -H 'If-Match: "1"' -H "Content-Type: application/json" \
  -d '{"name":"Kettle Pro","sku":"KT-01","price":29.90,"stock":12}' \
  "http://localhost:8080/api/products/1"
```

Response:

```http
HTTP/1.1 200 OK
ETag: "2"
X-Correlation-Id: c8ffce75-7570-4218-af38-c27ae37f2b2f
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 67

{"id":1,"name":"Kettle Pro","price":29.90,"sku":"KT-01","stock":12}
```

### 8c. `PATCH /api/products/1/stock` without preconditions (`200 OK`)

Request:

```bash
curl -i -s -X PATCH -H "X-Api-Key: demo-123" -H "Content-Type: application/json" \
  -d '{"delta":5}' \
  "http://localhost:8080/api/products/1/stock"
```

Response:

```http
HTTP/1.1 200 OK
X-Correlation-Id: 6e2ccbf1-55bf-414e-9363-6a177be7126f
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 67

{"id":1,"name":"Kettle Pro","price":29.90,"sku":"KT-01","stock":17}
```

---

## 9. Media-Type Versioning (`Accept: application/vnd.shop.v2+json`)

Request:

```bash
curl -i -s -H "Accept: application/vnd.shop.v2+json" "http://localhost:8080/api/products/1"
```

Response (`ProductV2` with `Money` object and `available` flag):

```http
HTTP/1.1 200 OK
ETag: "1"
Cache-Control: private, no-transform, max-age=60
X-Correlation-Id: 1ed93c57-6a77-4df5-841c-d5af9149f40d
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/vnd.shop.v2+json
Content-Length: 108

{"available":true,"id":1,"name":"Kettle","price":{"amount":24.90,"currency":"EUR"},"sku":"KT-01","stock":12}
```

---

## 10. Reviews Sub-Resource (`/api/products/{id}/reviews`)

### 10a. Create Review (`201 Created` + `Location`)

Request:

```bash
curl -i -s -X POST -H "X-Api-Key: demo-123" -H "Content-Type: application/json" \
  -d '{"stars":5,"author":"Sokha","text":"Boils fast and sturdy handle."}' \
  "http://localhost:8080/api/products/1/reviews"
```

Response:

```http
HTTP/1.1 201 Created
Location: http://localhost:8080/api/products/1/reviews/1
X-Correlation-Id: c1559e9a-937c-4d4a-bee2-9b58c5a26e77
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 88

{"author":"Sokha","id":1,"productId":1,"stars":5,"text":"Boils fast and sturdy handle."}
```

### 10b. List Reviews for Product 1 (`200 OK`)

Request:

```bash
curl -i -s "http://localhost:8080/api/products/1/reviews"
```

Response:

```http
HTTP/1.1 200 OK
X-Correlation-Id: 0ed2e1f1-99ad-460a-95fe-f4cea564b386
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 90

[{"author":"Sokha","id":1,"productId":1,"stars":5,"text":"Boils fast and sturdy handle."}]
```

### 10c. Unknown Product Sub-Resource Lookup (`404 Not Found`)

Request:

```bash
curl -i -s "http://localhost:8080/api/products/999/reviews"
```

Response:

```http
HTTP/1.1 404 Not Found
X-Correlation-Id: 4dee7f56-f8a8-4335-99cd-1df3232b28c3
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/problem+json
Content-Length: 127

{"detail":"Product 999 not found","instance":"/api/products/999/reviews","status":404,"title":"Not Found","type":"about:blank"}
```
