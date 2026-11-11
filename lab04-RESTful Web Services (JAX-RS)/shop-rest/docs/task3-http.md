# Task 3: Parameters, Validation, and RFC 9457 Problem Details — HTTP Transcript

## 1. Filtered and Sorted List (`GET /api/products?minPrice=5%20EUR&inStock=true&sort=price,desc`)

```http
$ curl -i 'http://localhost:8080/api/products?minPrice=5%20EUR&inStock=true&sort=price,desc'
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 125

[{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":12},{"id":2,"name":"Mug","price":6.50,"sku":"MG-01","stock":40}]
```

## 2. Invalid Money Query Parameter (`minPrice=abc` -> `400 problem+json`)

```http
$ curl -i 'http://localhost:8080/api/products?minPrice=abc'
HTTP/1.1 400 Bad Request
Content-Type: application/problem+json
Connection: close
Content-Length: 125

{"detail":"Cannot parse Money from 'abc'","instance":"/api/products","status":400,"title":"Bad Request","type":"about:blank"}
```

## 3. Unknown Sort Field (`sort=colour,asc` -> `400 problem+json`)

```http
$ curl -i 'http://localhost:8080/api/products?sort=colour,asc'
HTTP/1.1 400 Bad Request
Content-Type: application/problem+json
Connection: close
Content-Length: 121

{"detail":"Unknown sort field colour","instance":"/api/products","status":400,"title":"Bad Request","type":"about:blank"}
```

## 4. Bean Validation Errors on `POST` (`400 problem+json` with field errors)

```http
$ curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"","sku":"kettle","price":0,"stock":-1}'
HTTP/1.1 400 Bad Request
Content-Type: application/problem+json
Connection: close
Content-Length: 367

{"detail":"Validation failed","errors":[{"message":"must not be blank","field":"name"},{"message":"must be greater than or equal to 0.01","field":"price"},{"message":"must match \"[A-Z]{2}-\\d{2}\"","field":"sku"},{"message":"must be greater than or equal to 0","field":"stock"}],"instance":"/api/products","status":400,"title":"Invalid request","type":"about:blank"}
```

## 5. Missing Resource (`GET /api/products/99` -> `404 problem+json`)

```http
$ curl -i http://localhost:8080/api/products/99
HTTP/1.1 404 Not Found
Content-Type: application/problem+json
Content-Length: 117

{"detail":"Product 99 not found","instance":"/api/products/99","status":404,"title":"Not Found","type":"about:blank"}
```

## 6. Internal Server Error Experiment (500 `problem+json` without stack trace)

Simulated by temporarily throwing `IllegalStateException("Simulated internal failure")` on `GET /api/products/0`:

```http
$ curl -i http://localhost:8080/api/products/0
HTTP/1.1 500 Internal Server Error
Content-Type: application/problem+json
Connection: close
Content-Length: 154

{"detail":"reference 7bce0887-8e47-4a84-bb10-1bef5005aeba","instance":"/api/products/0","status":500,"title":"Internal Server Error","type":"about:blank"}
```
