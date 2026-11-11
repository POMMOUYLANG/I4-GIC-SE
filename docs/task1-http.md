# Task 1: First Resource and Server — HTTP Transcript

Server startup:

```bash
$ mvn -q compile exec:java
shop-rest listening on http://localhost:8080/api/
```

## 1. List all products (`GET /api/products`)

```http
$ curl -i http://localhost:8080/api/products
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 220

[{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":12,"version":1},{"id":2,"name":"Mug","price":6.50,"sku":"MG-01","stock":40,"version":1},{"id":3,"name":"Tea","price":4.20,"sku":"TE-01","stock":0,"version":1}]
```

## 2. Get single existing product (`GET /api/products/2`)

```http
$ curl -i http://localhost:8080/api/products/2
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 71

{"id":2,"name":"Mug","price":6.50,"sku":"MG-01","stock":40,"version":1}
```

## 3. Get unknown product ID (`GET /api/products/99`)

Explicit `NotFoundException` thrown by `ProductResource.get(long)`:

```http
$ curl -i http://localhost:8080/api/products/99
HTTP/1.1 404 Not Found
Content-Length: 0
```

## 4. Get non-numeric ID (`GET /api/products/abc`)

Rejected by Jersey routing because `{id: \d+}` does not match:

```http
$ curl -i http://localhost:8080/api/products/abc
HTTP/1.1 404 Not Found
Content-Length: 0
```
