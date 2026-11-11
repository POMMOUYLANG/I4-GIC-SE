# Task 4: Providers, Filters, and Interceptors — HTTP Transcript

## 1. POST without `X-Api-Key` (`401 Unauthorized` + `problem+json` + CORS + Correlation ID)

```http
$ curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Sample","sku":"SP-01","price":10.00,"stock":1}'
HTTP/1.1 401 Unauthorized
WWW-Authenticate: ApiKey realm="shop"
X-Correlation-Id: 425cc8f8-de8c-43e2-a13c-4fbe748047b8
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/problem+json
Content-Length: 125

{"detail":"Missing or invalid X-Api-Key","instance":"/api/products","status":401,"title":"Unauthorized","type":"about:blank"}
```

## 2. POST with invalid `X-Api-Key` (`401 Unauthorized`)

```http
$ curl -i -X POST http://localhost:8080/api/products \
  -H 'X-Api-Key: wrong-key' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Sample","sku":"SP-01","price":10.00,"stock":1}'
HTTP/1.1 401 Unauthorized
WWW-Authenticate: ApiKey realm="shop"
X-Correlation-Id: 283b8df8-be3e-4bf8-83c2-4664bb503ef5
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/problem+json
Content-Length: 125

{"detail":"Missing or invalid X-Api-Key","instance":"/api/products","status":401,"title":"Unauthorized","type":"about:blank"}
```

## 3. POST with valid `X-Api-Key` (`201 Created`)

```http
$ curl -i -X POST http://localhost:8080/api/products \
  -H 'X-Api-Key: demo-123' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Sample","sku":"SP-01","price":10.00,"stock":1}'
HTTP/1.1 201 Created
Location: http://localhost:8080/api/products/4
X-Correlation-Id: 9d14d03b-fd79-4c0d-a2c5-d5a3aa7d9664
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 62

{"id":4,"name":"Sample","price":10.00,"sku":"SP-01","stock":1}
```

## 4. CORS `OPTIONS` Preflight (`200 OK`)

```http
$ curl -i -X OPTIONS http://localhost:8080/api/products \
  -H 'Origin: http://localhost:5173' \
  -H 'Access-Control-Request-Method: POST'
HTTP/1.1 200 OK
X-Correlation-Id: ce826886-a9f3-46df-87fa-e417c9d9a2b1
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Length: 0
```

## 5. `GET` Echoing `X-Correlation-Id`

```http
$ curl -i http://localhost:8080/api/products/1 -H 'X-Correlation-Id: order-check-42'
HTTP/1.1 200 OK
X-Correlation-Id: order-check-42
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 63

{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":12}
```

## 6. `GET /api/products` with GZIP (`curl --compressed`)

```http
$ curl -i --compressed http://localhost:8080/api/products
HTTP/1.1 200 OK
X-Correlation-Id: d88eb99c-3245-4f7b-b60d-6b11380c9bc1
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Encoding: gzip
Vary: Accept-Encoding
Content-Type: application/json
Content-Length: 144

[{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":12},{"id":2,"name":"Mug","price":6.50,"sku":"MG-01","stock":40},{"id":4,"name":"Sample","price":10.00,"sku":"SP-01","stock":1},{"id":3,"name":"Tea","price":4.20,"sku":"TE-01","stock":0}]
```

## 7. `GET /api/products` without `Accept-Encoding` (No Compression)

```http
$ curl -i http://localhost:8080/api/products
HTTP/1.1 200 OK
X-Correlation-Id: 4fc81ad7-f2a1-427e-8922-669f9123b111
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE
Access-Control-Allow-Headers: Content-Type,X-Api-Key,X-Correlation-Id,If-Match
Access-Control-Expose-Headers: Location,ETag,Link,X-Total-Count,X-Correlation-Id
Content-Type: application/json
Content-Length: 247

[{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":12},{"id":2,"name":"Mug","price":6.50,"sku":"MG-01","stock":40},{"id":4,"name":"Sample","price":10.00,"sku":"SP-01","stock":1},{"id":3,"name":"Tea","price":4.20,"sku":"TE-01","stock":0}]
```

## Server Access Log & Filter Order Verification

```text
INFO: POST /api/products -> 401 [283b8df8-be3e-4bf8-83c2-4664bb503ef5] 0 ms
INFO: POST /api/products -> 201 [9d14d03b-fd79-4c0d-a2c5-d5a3aa7d9664] 83 ms
INFO: OPTIONS /api/products -> 200 [ce826886-a9f3-46df-87fa-e417c9d9a2b1] 0 ms
INFO: GET /api/products/1 -> 200 [order-check-42] 2 ms
INFO: GET /api/products -> 200 [d88eb99c-3245-4f7b-b60d-6b11380c9bc1] 5 ms
INFO: GET /api/products -> 200 [4fc81ad7-f2a1-427e-8922-669f9123b111] 0 ms
```
