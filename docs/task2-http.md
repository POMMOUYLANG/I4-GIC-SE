# Task 2: Full CRUD with Status Codes, Headers, JSON and XML — HTTP Transcript

## 1. Create product with JSON (`POST /api/products` -> `201 Created`)

```http
$ curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Teapot","sku":"TP-01","price":31.00,"stock":5}'
HTTP/1.1 201 Created
Location: http://localhost:8080/api/products/4
Content-Type: application/json
Content-Length: 62

{"id":4,"name":"Teapot","price":31.00,"sku":"TP-01","stock":5}
```

## 2. Duplicate SKU (`POST /api/products` -> `409 Conflict`)

```http
$ curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Teapot","sku":"TP-01","price":31.00,"stock":5}'
HTTP/1.1 409 Conflict
Content-Length: 0
```

## 3. XML Round Trip (`POST` XML -> `201`, `GET` XML -> `200`)

```http
$ curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/xml' \
  -d '<product><name>Bowl</name><sku>BW-01</sku><price>9.90</price><stock>3</stock></product>'
HTTP/1.1 201 Created
Location: http://localhost:8080/api/products/5
Content-Type: application/json
Content-Length: 59

{"id":5,"name":"Bowl","price":9.90,"sku":"BW-01","stock":3}
```

```http
$ curl -i http://localhost:8080/api/products/4 -H 'Accept: application/xml'
HTTP/1.1 200 OK
Content-Type: application/xml
Content-Length: 152

<?xml version="1.0" encoding="UTF-8" standalone="yes"?><product id="4"><name>Teapot</name><sku>TP-01</sku><price>31.00</price><stock>5</stock></product>
```

## 4. Stock Mutation (`PATCH /api/products/{id}/stock` -> `200` and `409`)

```http
$ curl -i -X PATCH http://localhost:8080/api/products/2/stock \
  -H 'Content-Type: application/json' -d '{"delta":-5}'
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 59

{"id":2,"name":"Mug","price":6.50,"sku":"MG-01","stock":35}
```

```http
$ curl -i -X PATCH http://localhost:8080/api/products/3/stock \
  -H 'Content-Type: application/json' -d '{"delta":-1}'
HTTP/1.1 409 Conflict
Content-Length: 0
```

## 5. Delete Product (`DELETE /api/products/4` -> `204` then `404`)

```http
$ curl -i -X DELETE http://localhost:8080/api/products/4
HTTP/1.1 204 No Content
```

```http
$ curl -i -X DELETE http://localhost:8080/api/products/4
HTTP/1.1 404 Not Found
Content-Length: 0
```

## 6. Unsupported Media Type (`Content-Type: text/plain` -> `415`)

```http
$ curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: text/plain' -d 'Teapot'
HTTP/1.1 415 Unsupported Media Type
Content-Length: 0
```

## 7. Replace Product (`PUT /api/products/4` -> `200`, `PUT /api/products/99` -> `404`)

```http
$ curl -i -X PUT http://localhost:8080/api/products/4 \
  -H 'Content-Type: application/json' \
  -d '{"name":"Teapot XL","sku":"TP-01","price":34.50,"stock":8}'
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 65

{"id":4,"name":"Teapot XL","price":34.50,"sku":"TP-01","stock":8}
```

```http
$ curl -i -X PUT http://localhost:8080/api/products/99 \
  -H 'Content-Type: application/json' \
  -d '{"name":"Missing","sku":"MS-01","price":1.00,"stock":1}'
HTTP/1.1 404 Not Found
Content-Length: 0
```
